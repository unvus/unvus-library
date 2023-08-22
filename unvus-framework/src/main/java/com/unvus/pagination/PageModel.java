package com.unvus.pagination;

import com.unvus.config.UnvusConstants;
import lombok.Data;

@Data
public class PageModel {
    private String queryString = "";
    private Integer currentPage = 1;
    private Integer dataPerPage = null;
    private Integer linkPerPage = UnvusConstants.DEFAULT_PAGE_LINK_COUNT;
    private Integer totalCnt = 0;
    private boolean skipCount = false;
}
