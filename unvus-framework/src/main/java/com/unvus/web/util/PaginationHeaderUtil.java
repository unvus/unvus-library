package com.unvus.web.util;

import com.unvus.pagination.PageModel;
import com.unvus.pagination.PaginationResult;
import com.unvus.pagination.PageSortModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;

/**
 * Created by guava on 16.6.24.
 */
@ConditionalOnWebApplication
@Component
public class PaginationHeaderUtil {
    private static final String PREFIX = "x-nv-";

    private static HttpServletResponse response;

    @Inject
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public static HttpHeaders generatePaginationHttpHeaders()
            throws URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        PageModel page = PaginationResult.pageModel.get();
        if(page == null) {
            return headers;
        }

        headers.add(PREFIX + "total-count", "" + page.getTotalCnt());
        headers.add(PREFIX + "data-count", "" + page.getDataPerPage());
        headers.add(PREFIX + "page", "" + page.getCurrentPage());

        return headers;
    }
    public static void setPaginationHttpHeaders() {
        PageModel page = PaginationResult.pageModel.get();
        if(page == null) {
            return;
        }
        response.setHeader(PREFIX + "total-count", "" + page.getTotalCnt());
        response.setHeader(PREFIX + "data-count", "" + page.getDataPerPage());
        response.setHeader(PREFIX + "page", "" + page.getCurrentPage());
    }

    public static HttpHeaders generatePaginationHttpHeaders(String baseUrl)
        throws URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        PageModel page = PaginationResult.pageModel.get();
        if(page == null) {
            return headers;
        }

        int totalCount = page.getTotalCnt();
        int currentPage = page.getCurrentPage();
        int dataPerPage = page.getDataPerPage();
        int totalPageLinkCnt = (totalCount - 1) / dataPerPage + 1;

        headers.add(PREFIX + "total-count", "" + totalCount);
        String link = "";
        if ((currentPage + 1) < totalPageLinkCnt) {
            link = "<" + generateUri(baseUrl, currentPage + 1, dataPerPage) + ">; rel=\"next\",";
        }
        // prev link
        if ((currentPage) > 0) {
            link += "<" + generateUri(baseUrl, currentPage - 1, dataPerPage) + ">; rel=\"prev\",";
        }
        // last and first link
        int lastPage = 0;
        if (totalPageLinkCnt > 0) {
            lastPage = totalPageLinkCnt - 1;
        }
        link += "<" + generateUri(baseUrl, lastPage, dataPerPage) + ">; rel=\"last\",";
        link += "<" + generateUri(baseUrl, 0, dataPerPage) + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }

    private static String generateUri(String baseUrl, int page, int size) throws URISyntaxException {
        return UriComponentsBuilder.fromUriString(baseUrl).queryParam("page", page).queryParam("size", size).toUriString();
    }
}
