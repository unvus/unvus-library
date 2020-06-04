package com.unvus.web.filter;


import com.unvus.config.UnvusConstants;
import com.unvus.config.mybatis.pagination.OrderBy;
import com.unvus.config.mybatis.pagination.Pagination;
import com.unvus.config.mybatis.pagination.tool.PagerTool;
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
 *
 * @author kim
 *
 */
public class PagingFilter extends OncePerRequestFilter {

    private String encoding = "UTF-8";

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Filter의 메인 메소드에 해당한다.
     * @see OncePerRequestFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)
     */
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Pagination.resetAll();

        if (request.getContentType() != null
                && request.getContentType().indexOf("multipart") != -1) {
            // contentType 이 파일 첨부라면 스킵한다.
            chain.doFilter(request, response);

        } else {

//            System.out.println("SESSION_ID : " + request.getSession().getId());

            String currentPage = request.getParameter(UnvusConstants.CURRENT_PAGE);
            String dataPerPage = request.getParameter(UnvusConstants.DATA_PER_PAGE);
            String linkPerPage = request.getParameter(UnvusConstants.LINK_PER_PAGE);
            // &orderBy=group_code:desc,last_modified_dt:asc
            String orderBy = request.getParameter(UnvusConstants.ORDER_BY);

            String[] orderByList = request.getParameterValues(UnvusConstants.ORDER_BY_LIST);
            if(orderByList == null) {
                orderByList = request.getParameterValues(UnvusConstants.ORDER_BY_LIST + "[]");
            }

            String skipCount = request.getParameter(UnvusConstants.SKIP_COUNT);

            if (StringUtils.isEmpty(currentPage)) {
                Pagination.currentPage.set(new Integer(1));
            } else {
                try {
                    Pagination.currentPage.set(new Integer(currentPage));
                } catch (Exception ignore) {
                    Pagination.currentPage.set(new Integer(1));
                }
            }

            if (StringUtils.isEmpty(dataPerPage)) {
                Pagination.dataPerPage.set(new Integer(UnvusConstants.DEFAULT_DATA_PER_PAGE));
            } else {
                try {
                    Pagination.dataPerPage.set(new Integer(dataPerPage));
                } catch (Exception ignore) {
                    Pagination.dataPerPage.set(new Integer(UnvusConstants.DEFAULT_DATA_PER_PAGE));
                }
            }

            if (StringUtils.isEmpty(linkPerPage)) {
                Pagination.linkPerPage.set(new Integer(UnvusConstants.DEFAULT_PAGE_LINK_COUNT));
            } else {
                try {
                    Pagination.linkPerPage.set(new Integer(linkPerPage));
                } catch (Exception ignore) {
                    Pagination.linkPerPage.set(new Integer(UnvusConstants.DEFAULT_PAGE_LINK_COUNT));
                }
            }

            if (StringUtils.isEmpty(skipCount)) {
                Pagination.skipCount.set(false);
            } else {
                Pagination.skipCount.set(true);
            }


            if (!StringUtils.isEmpty(orderBy)) {
                try {
                    List<OrderBy> orderList = new ArrayList<>();
                    String[] orderByArr = StringUtils.split(orderBy, ',');
                    for(String orderByItem : orderByArr) {
                        String[] keyValPair = StringUtils.split(orderByItem, ':');
                        OrderBy order = new OrderBy(keyValPair[0], OrderBy.SortDirection.valueOf(StringUtils.upperCase(keyValPair[1])));
                        orderList.add(order);
                       }
                    Pagination.orderBy.set(orderList);
                } catch (Exception ignore) {
                    Pagination.orderBy.set(null);
                  }
            }else if(orderByList != null && orderByList.length > 0) {
                try {
                    List<OrderBy> orderList = new ArrayList<>();
                    for(String orderByStr : orderByList) {
                        if(StringUtils.isNotBlank(orderByStr)) {
                            String[] keyValPair = StringUtils.split(orderByStr, ':');
                            OrderBy order = new OrderBy(keyValPair[0], OrderBy.SortDirection.valueOf(StringUtils.upperCase(keyValPair[1])));
                            orderList.add(order);
                           }
                       }
                    Pagination.orderBy.set(orderList);
                } catch (Exception ignore) {
                    Pagination.orderBy.set(null);
                }
            }


            Pagination.queryString.set(extractQueryString(request));
            request.setAttribute(UnvusConstants.PAGER_TOOL, new PagerTool());
            request.setAttribute("cond", getCondMap(request));

            chain.doFilter(request, response);


        }

    }

    /**
     * 파라미터로 부터 query string 을 추출한다.
     * 파라미터가 없을 경우에는 "?" 를 반환
     * 파라미터가 있을 경우에는 "?" 로 시작해서 "&" 로 끝나는 문자열을 반환
     *
     * @param request the request
     *
     * @return the string
     */
    @SuppressWarnings("rawtypes")
    public String extractQueryString(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("?");

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            // Constants.CURRENT_PAGE 이거나 Constants.DATA_PER_PAGE 인 경우는 skip
            if (StringUtils.equals(UnvusConstants.CURRENT_PAGE, key) || StringUtils.equals(UnvusConstants.DATA_PER_PAGE, key)) {
                continue;
             }
            if(key.startsWith("q.") || key.startsWith("q.commonSelect") || key.startsWith("q.commonText") || StringUtils.equals(key, "skin")) {
                String[] values = (String[])request.getParameterValues(key);
                for (int i = 0; i < values.length; i++) {
                    try {
                        sb.append(key).append("=").append(URLEncoder.encode(values[i], encoding)).append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                      }
                 }
            }
        }

        return sb.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map getCondMap(HttpServletRequest request) {
        Enumeration enumeration = request.getParameterNames();
        Map condMap = new HashMap();

        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            if(key.startsWith(UnvusConstants.CONDITION_PARAM_KEY)) {
                String[] values = (String[])request.getParameterValues(key);
                condMap.put(key.substring(UnvusConstants.CONDITION_PARAM_KEY.length()), values[0]);
            }
        }

       return condMap;
    }
}
