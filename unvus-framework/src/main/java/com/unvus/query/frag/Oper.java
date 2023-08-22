package com.unvus.query.frag;

import com.fasterxml.jackson.annotation.JsonValue;
import com.unvus.query.OperToConditionFunc;
import org.mybatis.dynamic.sql.SqlBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum Oper {
    // EQuals
    EQ("eq", "=", (value) -> {
        if(value instanceof Collection<?> && ((List)value).size() > 0) {
            Object firstItem = ((List)value).get(0);
            if(firstItem instanceof String) {
                List<String> arr = new ArrayList<>();
                for(Object item : (List)value) {
                    arr.add((String)item);
                }
                return SqlBuilder.isIn(arr);
            }
            if(firstItem instanceof Number) {
                List<Number> arr = new ArrayList<>();
                for(Object item : (List)value) {
                    arr.add((Number)item);
                }
                return SqlBuilder.isIn(arr);
            }
            return SqlBuilder.isIn(value);
        }else {
            return SqlBuilder.isEqualTo(value);
        }
    }),
    // Greater Than ">"
    GT("gt", ">", (value) -> {
        return SqlBuilder.isGreaterThan(value);
    }),
    // Less Than "<"
    LT("lt", "<", (value) -> {
        return SqlBuilder.isLessThan(value);
    }),
    // Greater Than or Equals ">="
    GTE("gte", ">=", (value) -> {
        return SqlBuilder.isGreaterThanOrEqualTo(value);
    }),
    // Less Than or Equals "<="
    LTE("lte", "<=", (value) -> {
        return SqlBuilder.isLessThanOrEqualTo(value);
    }),
    // NOT "<>"
    NOT("not", "<>", (value) -> {
        return SqlBuilder.isNotEqualTo(value);
    }),
    // IS
    IS("is", "IS", (value) -> {
        return SqlBuilder.isNull();
    }),
    // IS
    IS_NOT("isnot", "IS NOT", (value) -> {
        return SqlBuilder.isNotNull();
    }),
    // IN
    IN("in", "IN", (value) -> {
        return SqlBuilder.isIn((Collection)value);
    }),
    // NOT IN
    NOT_IN("ni", "NOT IN", (value) -> {
        return SqlBuilder.isNotIn((Collection)value);
    }),
    // LIKE '%keyword%'
    LIKE("lk", "LIKE", (value) -> {
        return SqlBuilder.isLike(value);
    }),
    // LIKE '%keyword%'
    LIKE_FULL("lf", "LIKE", (value) -> {
        return SqlBuilder.isLike("%" + value + "%");
    }),
    // LIKE 'keyword%'
    LIKE_AFTER("la", "LIKE", (value) -> {
        return SqlBuilder.isLike(value + "%");
    }),
    // LIKE '%keyword'
    LIKE_BEFORE("lb", "LIKE", (value) -> {
        return SqlBuilder.isLike("%" + value);
    }),
    // Greater Than
    DATE_FROM("d_gt", ">", (value) -> {
        return SqlBuilder.isGreaterThanOrEqualTo(value);
    }),
    // Less Than
    DATE_TO("d_lt", "<", (value) -> {
        return SqlBuilder.isLessThanOrEqualTo(value);
    })
    ;


    final String code;
    final String symbol;
    final OperToConditionFunc func;

    Oper(String code, String symbol, OperToConditionFunc func) {
        this.code = code;
        this.symbol = symbol;
        this.func = func;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static Oper getByCode(String code) {
        for(Oper c : values()) {
            if(c.code.equals(code)) return c;
        }
        return null;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public OperToConditionFunc getFunc() {
        return this.func;
    }
}
