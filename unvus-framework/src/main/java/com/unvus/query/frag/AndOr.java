package com.unvus.query.frag;

import com.unvus.domain.EnumCode;
import com.unvus.query.QueryItem;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AndOr implements EnumCode, QueryItem {
    AND("and"),
    // Greater Than
    OR("or");

    final String code;

    AndOr(String code) {
        this.code = code;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    public static AndOr getByCode(String code) {
        for(AndOr c : values()) {
            if(c.code.equals(code)) return c;
        }
        return null;
    }
}
