package com.unvus.pagination;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class SortBy implements Serializable {
    public enum SortDirection {
        ASC, DESC, FIELD
    }

    private String sortKey;
    private SortDirection sortValue;
    private boolean checked = true;

    private List<Object> fieldList;

    public SortBy(String sortKey) {
        this.sortKey = sortKey;
        this.sortValue = SortDirection.ASC;
    }

    public SortBy(String sortKey, SortDirection sortValue) {
        assert sortValue != SortDirection.FIELD;

        this.sortKey = sortKey;
        this.sortValue = sortValue;
    }

    public SortBy(String sortKey, List fieldList) {
        this.sortKey = sortKey;
        this.fieldList = fieldList;
        this.sortValue = SortDirection.FIELD;
    }

    public String getSortKeyColumnName() {
        String columnName = StringUtils.substringAfterLast(sortKey, ".");
        return (StringUtils.isBlank(columnName))? sortKey :columnName;
    }
}
