package com.unvus.config.mybatis.pagination;

import com.unvus.config.UnvusConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

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

        if(method != null) {
            pageable = method.getAnnotation(Pageable.class);

            if (pageable != null) {
                String skipPagingKey = pageable.skipPagingKey();
                usePaging = perpareToPaging(params, skipPagingKey);
            }else {
                perpareToSort(params);
            }
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
                    param.put(pageable.skipPagingKey(), true);
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

        if(usePaging && Boolean.FALSE.equals(Pagination.skipCount.get())) {

            String cntMethodName = ms.getId() + "Cnt";

            if(StringUtils.isNotBlank(pageable.countMapperId())) {
                cntMethodName = pageable.countMapperId();
            }

            MappedStatement countMs = ms.getConfiguration().getMappedStatement(cntMethodName);

            if(countMs != null) {

                args[MAPPED_STATEMENT_INDEX] = countMs;

                List countResult = (List)invocation.proceed();

                int resultCnt = (int)countResult.get(0);

                try {
                    Pagination.totalCnt.set(resultCnt);
                } catch (Exception ignore) {
                    Pagination.totalCnt.set(0);
                }

                if (result != null) {
                    int idx = 1;
                    int base = Pagination.totalCnt.get() - getFromData();
                    for(Iterator it = ((List)result).iterator(); it.hasNext(); idx--) {
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

        }

        return result;
    }


    /**
     * Perpare to paging.
     * @param args
     * @param skipPagingKey
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean perpareToPaging(Object args, String skipPagingKey) {
        List<Map> maps = arrangeParams(args);

        if(maps.size() > 0) {
            Map<String, Object> map = (Map<String, Object>)maps.get(0);
            if(map.containsKey("fromData") && map.containsKey("toData")) {
                return false;
            }

            if(map.containsKey(skipPagingKey) && Boolean.valueOf(map.get(skipPagingKey).toString())) {
                return false;
            }
            if(Pagination.skipPaging.get() != null && Pagination.skipPaging.get()) {
                return false;
            }

            Integer currentPage = Pagination.currentPage.get();
            Integer dataPerPage = Pagination.dataPerPage.get();
            List<OrderBy> orderByList = Pagination.orderBy.get();
            List<String> projections = Pagination.projections.get();

            if (currentPage == null) {
                currentPage = 1;
            }

            if (dataPerPage == null) {
                dataPerPage = UnvusConstants.DEFAULT_DATA_PER_PAGE;
            }

            int fromData = (dataPerPage * (currentPage - 1)) + 1;
            int toData = fromData + dataPerPage - 1;

            for(Map<String, Object> param : maps) {
                param.put("fromData", fromData - 1);
                param.put("toData", toData);
                param.put("dataPerPage", dataPerPage);
                param.put("currentPage", currentPage);

                if(CollectionUtils.isNotEmpty(orderByList)){
                    param.put("orderByList", orderByList);
                }
                if(CollectionUtils.isNotEmpty(projections)){
                    param.put("projections", projections);
                    if(Boolean.TRUE.equals(Pagination.projectionToJoin.get())) {
                        param.put("joinProjections", projections);
                    }
                }
            }
            return true;
        }
        return false;

    }

    private void perpareToSort(Object args) {
        List<Map> maps = arrangeParams(args);

        if(maps.size() > 0) {
            List<OrderBy> orderByList = Pagination.orderBy.get();
            if(CollectionUtils.isNotEmpty(orderByList)){
                for(Map<String, Object> param : maps) {
                    param.put("orderByList", orderByList);
                }
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

    private int getFromData() {
        Integer currentPage = Pagination.currentPage.get();
        Integer dataPerPage = Pagination.dataPerPage.get();

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
