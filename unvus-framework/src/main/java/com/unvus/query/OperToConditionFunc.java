package com.unvus.query;

import org.mybatis.dynamic.sql.VisitableCondition;

@FunctionalInterface
public interface OperToConditionFunc {
    VisitableCondition apply(Object value);
}
