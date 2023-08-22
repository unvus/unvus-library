package com.unvus.util;

import java.util.LinkedHashMap;

public class FieldMap extends LinkedHashMap<String, Object> {

    public static FieldMap of(String key, Object value) {
        FieldMap fieldMap = new FieldMap();
        fieldMap.put(key, value);
        return fieldMap;
    }

    public FieldMap add(String key, Object value) {
        put(key, value);
        return this;
    }
}
