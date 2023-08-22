package com.unvus.pagination;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class SortModel {

    private List<SortBy> sortByList = new ArrayList<>();

    public SortModel addSortBy(SortBy... sortBy) {
        Collections.addAll(this.sortByList, sortBy);
        return this;
    }

    public SortModel setSortBy(SortBy... sortBy) {
        this.sortByList = Arrays.asList(sortBy);
        return this;
    }

    public SortModel resetSortBy() {
        this.sortByList = new ArrayList<>();
        return this;
    }
}
