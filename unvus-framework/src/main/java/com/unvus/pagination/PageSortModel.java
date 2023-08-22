package com.unvus.pagination;

import com.unvus.config.UnvusConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class PageSortModel {

    private Page page;

    private String queryString = "";
    private List<SortBy> sortByList = new ArrayList<>();


    public PageSortModel(boolean usePaging) {
        if(usePaging) {
            this.page = new Page();
        }
    }

    public PageSortModel addSortBy(SortBy... sortBy) {
        Collections.addAll(this.sortByList, sortBy);
        return this;
    }

    public PageSortModel setSortBy(SortBy... sortBy) {
        this.sortByList = Arrays.asList(sortBy);
        return this;
    }

    public PageSortModel resetSortBy() {
        this.sortByList = new ArrayList<>();
        return this;
    }


    @Data
    public static class Page {
        private Integer currentPage = 1;
        private Integer dataPerPage = UnvusConstants.DEFAULT_DATA_PER_PAGE;
        private Integer linkPerPage = UnvusConstants.DEFAULT_PAGE_LINK_COUNT;
        private Integer totalCnt = 0;
        private boolean skipCount = false;
    }

}
