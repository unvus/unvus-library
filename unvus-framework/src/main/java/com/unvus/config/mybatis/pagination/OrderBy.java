package com.unvus.config.mybatis.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class OrderBy implements Serializable {
    public enum SortDirection {
        ASC, DESC
    }

    private String orderKey;
    private SortDirection orderValue;

    public String getOrderKeyColumnName() {
        String columnName = StringUtils.substringAfterLast(orderKey, ".");
        return (StringUtils.isBlank(columnName))?orderKey:columnName;
    }
}
