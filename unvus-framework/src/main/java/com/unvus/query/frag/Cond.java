package com.unvus.query.frag;

import com.unvus.query.QueryItem;

import lombok.Data;

@Data
public class Cond implements QueryItem {
    private final AndOr andOr;
    private final Field field;
    private final Oper oper;
    private final Object val;

    public Cond(Field field, Object val) {
        this(field, val, Oper.EQ);
    }

    public Cond(Field field, Object val, Oper oper) {
        this(field, val, oper, AndOr.AND);
    }

    public Cond(Field field, Object val, Oper oper, AndOr andOr) {
        this.andOr = andOr;
        this.field = field;
        this.oper = oper;
        this.val = val;
    }
}
