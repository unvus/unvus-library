package com.unvus.config.mybatis.pagination;

import com.unvus.config.UnvusConstants;
import com.unvus.pagination.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({
    @Signature(type = Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PaginationInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(PaginationInterceptor.class);

    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
        Object params = args[PARAMETER_INDEX];

        Class klass = Class.forName(StringUtils.substringBeforeLast(ms.getId(), "."));
        Method method = null;

        String methodName = StringUtils.substringAfterLast(ms.getId(), ".");

        if(!StringUtils.contains(methodName, "!")) {    // insert 할때 someMethod!selectKey

            Method[] methods = klass.getDeclaredMethods();
            for(Method methodSample : methods) {
                if(StringUtils.equals(methodSample.getName(), methodName)) {
                    method = methodSample;
                    break;
                }
            }
        }

        boolean usePaging = false;

        Pageable pageable = null;

        PageModel pageModel = Pagination.pageModel.get();

        if(method != null) {
            pageable = method.getAnnotation(Pageable.class);

            if(pageModel != null) {
                if (pageable != null) {
                    String usePagingKey = pageable.usePagingKey();
                    usePaging = prepareToPaging(params, usePagingKey);
                }
            }
            prepareToSort(params);
        }

        Object result;

        if(usePaging && pageable.useMergeQuery()) {

            String mergeMapperId = ms.getId() + "Ids";

            if(StringUtils.isNotBlank(pageable.mergeMapperId())) {
                mergeMapperId = pageable.mergeMapperId();
            }

            MappedStatement originalMs = (MappedStatement)args[MAPPED_STATEMENT_INDEX];

            MappedStatement mergeMapperMs = ms.getConfiguration().getMappedStatement(mergeMapperId);
            args[MAPPED_STATEMENT_INDEX] = mergeMapperMs;

            List mergeIdList = (List)invocation.proceed();

            result = new ArrayList();

            if(CollectionUtils.isNotEmpty(mergeIdList)) {
                List<Map> maps = arrangeParams(params);

                for(Map<String, Object> param : maps) {
                    param.put(pageable.mergeParamId(), mergeIdList);
                    param.put(pageable.usePagingKey(), false);
                }

                args[MAPPED_STATEMENT_INDEX] = originalMs;
                result = invocation.proceed();

                for(Map<String, Object> param : maps) {
                    param.remove(pageable.mergeParamId());
                }
            }

        }else {
            result = invocation.proceed();
        }

        if(pageModel != null && pageable != null) {
            resetPaging(args, pageable.usePagingKey());
        }

        if(usePaging && Boolean.FALSE.equals(pageModel.isSkipCount())) {

            String cntMethodName = ms.getId() + "Cnt";

            if(StringUtils.isNotBlank(pageable.countMapperId())) {
                cntMethodName = pageable.countMapperId();
            }

            MappedStatement countMs = ms.getConfiguration().getMappedStatement(cntMethodName);

            if(countMs != null) {
                int resultCnt = 0;

                if(result != null) {
                    if(((List) result).size() < pageModel.getDataPerPage()) {
                        if(pageModel.getCurrentPage().equals(1)) {
                            resultCnt = ((List) result).size();
                        }else {
                            resultCnt = (pageModel.getCurrentPage() - 1) * pageModel.getDataPerPage() + ((List) result).size();
                        }
                    }else {
                        args[MAPPED_STATEMENT_INDEX] = countMs;

                        List countResult = (List)invocation.proceed();

                        resultCnt = (int)countResult.get(0);
                    }
                }

                setCountResult(pageModel, result, resultCnt);
            }

        }

        // 초기화
        if(Boolean.TRUE.equals(Pagination.keepAsResult.get())) {
            PaginationResult.pageModel.set(Pagination.pageModel.get());
            PaginationResult.sortModel.set(Pagination.sortModel.get());
        }

        Pagination.reset();

        return result;
    }

    private void setCountResult(PageModel pageModel, Object result, int resultCnt) {
        try {
            pageModel.setTotalCnt(resultCnt);
        } catch (Exception ignore) {
            pageModel.setTotalCnt(0);
        }

        if (result != null) {
            int idx = 1;
            int base = pageModel.getTotalCnt() - getFromData(pageModel);
            for(Iterator it = ((List) result).iterator(); it.hasNext(); idx--) {
                Object obj = it.next();
                if(obj instanceof Countable) {
                    Countable countable = (Countable)obj;
                    countable.setPositionIdx(base + idx);
                }else if(obj instanceof Map) {
                    ((Map)obj).put("positionIdx", base + idx);
                }else {
                    break;
                }
            }
        }
    }


    /**
     * Perpare to paging.
     * @param args
     * @param usePagingKey
     * @return use: true, skip: false
     */
    @SuppressWarnings("unchecked")
    private boolean prepareToPaging(Object args, String usePagingKey) {
        PageModel pageModel = Pagination.pageModel.get();
        SortModel sortModel = Pagination.sortModel.get();
        List<Map> maps = arrangeParams(args);

        if(maps.size() > 0) {

            if(pageModel != null
                && pageModel.getDataPerPage() != null) {

                Integer currentPage = pageModel.getCurrentPage();
                Integer dataPerPage = pageModel.getDataPerPage();

                if (currentPage == null) {
                    currentPage = 1;
                }

                if (dataPerPage == null) {
                    dataPerPage = UnvusConstants.DEFAULT_DATA_PER_PAGE;
                }

                int fromData = (dataPerPage * (currentPage - 1)) + 1;
                int toData = fromData + dataPerPage - 1;

                for(Map<String, Object> param : maps) {
                    param.put("_fromData", fromData - 1);
                    param.put("_toData", toData);
                    param.put("_dataPerPage", dataPerPage);
                    param.put("_currentPage", currentPage);
                    param.put(usePagingKey, true);
                }
                return true;
            }
        }
        return false;

    }

    private void prepareToSort(Object args) {
        List<Map> maps = arrangeParams(args);
        SortModel sortModel = Pagination.sortModel.get();

        if(sortModel != null && maps.size() > 0) {
            List<SortBy> orderByList = sortModel.getSortByList();
            if(CollectionUtils.isNotEmpty(orderByList)){
                for(Map<String, Object> param : maps) {
                    param.put("_sortByList", orderByList);
                }
            }
        }
    }

    private void resetPaging(Object args, String usePagingKey) {
        List<Map> maps = arrangeParams(args);

        if(maps.size() > 0) {
            for(Map<String, Object> param : maps) {
                param.remove("_fromData");
                param.remove("_toData");
                param.remove("_dataPerPage");
                param.remove("_currentPage");
                param.remove(usePagingKey);
            }
        }
    }

    private List<Map> arrangeParams(Object args) {
        List<Map> maps = new ArrayList<Map>();
        if(args instanceof org.apache.ibatis.binding.MapperMethod.ParamMap) {
            for(Object val : ((Map)args).values()) {
                if(val instanceof Map) {
                    maps.add((Map)val);
                }
            }

        }else if(args instanceof Map) {
            maps.add((Map)args);
        }
        return maps;
    }

    private int getFromData(PageModel page) {
        Integer currentPage = page.getCurrentPage();
        Integer dataPerPage = page.getDataPerPage();

        if (currentPage == null) {
            currentPage = 1;
        }

        if (dataPerPage == null) {
            dataPerPage = UnvusConstants.DEFAULT_DATA_PER_PAGE;
        }

        return (dataPerPage * (currentPage - 1)) + 1;
    }


    private String getCountSql(String sql) {
        String lowerCaseSql = sql.toLowerCase().replace("\n", " ").replace("\t", " ");
        int index = lowerCaseSql.indexOf(" order ");
        if(index != -1) {
            sql = sql.substring(0, index);
        }
        return "SELECT COUNT(*) FROM (select 1 as col_c " + sql.substring(lowerCaseSql.indexOf(" from ")) + " )  cnt";
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        log.info("properties => {}", properties);
    }

}
