package com.unvus.query;

import com.unvus.query.frag.Field;

public interface FieldSet {
    String prefix();
    Field getField(String property);
}
