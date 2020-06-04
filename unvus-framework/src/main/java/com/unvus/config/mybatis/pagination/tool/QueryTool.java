package com.unvus.config.mybatis.pagination.tool;

import com.unvus.config.mybatis.pagination.OrderBy;
import com.unvus.config.mybatis.pagination.Pagination;

import com.unvus.config.mybatis.pagination.Sortable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QueryTool {

    public static boolean hasOrderBy() {
        List<OrderBy> orderByList = Pagination.orderBy.get();
        if(CollectionUtils.isNotEmpty(orderByList)) {
            return true;
        }
        return false;
    }

    public static boolean hasOrderBy(String key) {
        List<OrderBy> orderByList = Pagination.orderBy.get();
        if(CollectionUtils.isNotEmpty(orderByList)) {
            for(OrderBy orderBy: orderByList) {
                if(StringUtils.equalsIgnoreCase(orderBy.getOrderKeyColumnName(), key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static OrderBy getOrderBy(String key) {
        List<OrderBy> orderByList = Pagination.orderBy.get();
        if(CollectionUtils.isNotEmpty(orderByList)) {
            for(OrderBy orderBy: orderByList) {
                if(StringUtils.equalsIgnoreCase(orderBy.getOrderKeyColumnName(), key)) {
                    return orderBy;
                }
            }
        }
        return null;
    }

    public static void addOrderBy(OrderBy... orderBy) {
        List<OrderBy> orderByList = Pagination.orderBy.get();
        if(CollectionUtils.isEmpty(orderByList)) {
            orderByList = new ArrayList<>();
            Pagination.orderBy.set(orderByList);
        }
        Collections.addAll(orderByList, orderBy);
    }

    public static void setOrderBy(OrderBy... orderBy) {
        List<OrderBy> orderByList = new ArrayList<>();
        Collections.addAll(orderByList, orderBy);
        Pagination.orderBy.set(orderByList);
    }

    public static void resetOrderBy() {
        List<OrderBy> orderByList = new ArrayList<>();
        Pagination.orderBy.set(orderByList);
    }

    public static void setDefaultOrderBy(OrderBy... orderBy) {
        if(!hasOrderBy()) {
            setOrderBy(orderBy);
        }
    }

    public static void sort(List<? extends Sortable> list) {
        if(list != null) {
            Collections.sort(list, Comparator.comparingInt(Sortable::getSortOrdr));
        }
    }

//    public static void addCondition(Map<String, Object> param, ) {
//
//    }
}
