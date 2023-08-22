package com.unvus.web.filter;

import com.unvus.config.UnvusConstants;
import com.unvus.pagination.*;
import com.unvus.pagination.util.PageUiHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author kim
 */
public class PagingFilter extends OncePerRequestFilter {

    private String encoding = "UTF-8";
    private int defaultDataPerPage = UnvusConstants.DEFAULT_DATA_PER_PAGE;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setDefaultDataPerPage(int defaultDataPerPage) {
        this.defaultDataPerPage = defaultDataPerPage;
    }

    protected void initFilterBean() throws ServletException {

    }

    /**
     * Filter의 메인 메소드에 해당한다.
     *
     * @see OncePerRequestFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)
     */
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        if (request.getContentType() != null
            && request.getContentType().indexOf("multipart") != -1) {
            // contentType 이 파일 첨부라면 스킵한다.
            try {
                chain.doFilter(request, response);
            } finally {
                Pagination.reset();
                PaginationResult.reset();
            }
            return;
        }


//        PageSortModel pageSortModel = new PageSortModel(true);
//        PageSortModel.Page page = pageSortModel.getPage();
        PageModel pageModel = new PageModel();
        SortModel sortModel = new SortModel();

        String currentPage = request.getParameter(UnvusConstants.CURRENT_PAGE);
        String dataPerPage = request.getParameter(UnvusConstants.DATA_PER_PAGE);
        String linkPerPage = request.getParameter(UnvusConstants.LINK_PER_PAGE);
        boolean usePaging = !("false".equals(request.getParameter(UnvusConstants.USE_PAGING)));
        request.setAttribute(UnvusConstants.USE_PAGING, usePaging);


        // &orderBy=group_code:desc,last_modified_dt:asc
        String orderBy = request.getParameter(UnvusConstants.SORT_BY);

        String[] orderByList = request.getParameterValues(UnvusConstants.SORT_BY_LIST);
        if (orderByList == null) {
            orderByList = request.getParameterValues(UnvusConstants.SORT_BY_LIST + "[]");
        }

        String skipCount = request.getParameter(UnvusConstants.SKIP_COUNT);

        if (StringUtils.isNotEmpty(currentPage)) {
            try {
                pageModel.setCurrentPage(new Integer(currentPage));
            } catch (Exception ignore) {
                pageModel.setCurrentPage(new Integer(1));
            }
        }

        if (!StringUtils.isEmpty(dataPerPage)) {
            try {
                pageModel.setDataPerPage(new Integer(dataPerPage));
            } catch (Exception ignore) {
            }
        }

        if (StringUtils.isEmpty(linkPerPage)) {
            pageModel.setLinkPerPage(new Integer(UnvusConstants.DEFAULT_PAGE_LINK_COUNT));
        } else {
            try {
                pageModel.setLinkPerPage(new Integer(linkPerPage));
            } catch (Exception ignore) {
                pageModel.setLinkPerPage(new Integer(UnvusConstants.DEFAULT_PAGE_LINK_COUNT));
            }
        }

        if (!StringUtils.isEmpty(skipCount)) {
            pageModel.setSkipCount(true);
        }

        if (!StringUtils.isEmpty(orderBy)) {
            try {
                List<SortBy> orderList = new ArrayList<>();
                String[] orderByArr = StringUtils.split(orderBy, ',');
                for (String orderByItem : orderByArr) {
                    String[] keyValPair = StringUtils.split(StringUtils.trim(orderByItem), ':');
                    SortBy sort = new SortBy(StringUtils.trim(keyValPair[0]), SortBy.SortDirection.valueOf(StringUtils.trim(StringUtils.upperCase(keyValPair[1]))));
                    sort.setChecked(false);
                    orderList.add(sort);
                }
                sortModel.setSortByList(orderList);
            } catch (Exception ignore) {
                sortModel.resetSortBy();
            }
        } else if (orderByList != null && orderByList.length > 0) {
            try {
                List<SortBy> orderList = new ArrayList<>();
                for (String orderByStr : orderByList) {
                    if (StringUtils.isNotBlank(orderByStr)) {
                        String[] keyValPair = StringUtils.split(StringUtils.trim(orderByStr), ':');
                        SortBy sort = new SortBy(StringUtils.trim(keyValPair[0]), SortBy.SortDirection.valueOf(StringUtils.trim(StringUtils.upperCase(keyValPair[1]))));
                        sort.setChecked(false);
                        orderList.add(sort);
                    }
                }
                sortModel.setSortByList(orderList);
            } catch (Exception ignore) {
                sortModel.resetSortBy();
            }
        }
        pageModel.setQueryString(extractQueryString(request));
        request.setAttribute(UnvusConstants.PAGE_MODEL, pageModel);
        request.setAttribute(UnvusConstants.SORT_MODEL, sortModel);
//        request.setAttribute("cond", getCondMap(request));

        try {
            request.setAttribute(UnvusConstants.PAGER_TOOL, new PageUiHelper());
            chain.doFilter(request, response);
        } finally {
            Pagination.reset();
            PaginationResult.reset();
        }

    }

    /**
     * 파라미터로 부터 query string 을 추출한다.
     * 파라미터가 없을 경우에는 "?" 를 반환
     * 파라미터가 있을 경우에는 "?" 로 시작해서 "&" 로 끝나는 문자열을 반환
     *
     * @param request the request
     * @return the string
     */
    @SuppressWarnings("rawtypes")
    public String extractQueryString(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("?");

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            // Constants.CURRENT_PAGE 이거나 Constants.DATA_PER_PAGE 인 경우는 skip
            if (StringUtils.equalsAny(key,
                    UnvusConstants.CURRENT_PAGE,
                    UnvusConstants.DATA_PER_PAGE,
                    UnvusConstants.LINK_PER_PAGE,
                    UnvusConstants.SORT_BY,
                    UnvusConstants.SORT_BY_LIST)) {
                continue;
            }
            String[] values = request.getParameterValues(key);
            for (int i = 0; i < values.length; i++) {
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(values[i], encoding)).append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map getCondMap(HttpServletRequest request) {
        Enumeration enumeration = request.getParameterNames();
        Map condMap = new HashMap();

        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if (key.startsWith(UnvusConstants.CONDITION_PARAM_KEY)) {
                String[] values = (String[]) request.getParameterValues(key);
                condMap.put(key.substring(UnvusConstants.CONDITION_PARAM_KEY.length()), values[0]);
            }
        }

        return condMap;
    }

}
