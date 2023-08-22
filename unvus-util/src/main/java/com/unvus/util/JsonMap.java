package com.unvus.util;

/**
 * ClobJsonTypeHandler 용 클래스
 */
public class JsonMap extends FieldMap {
    public static JsonMap of(String key, Object value) {
        JsonMap fieldMap = new JsonMap();
        fieldMap.put(key, value);
        return fieldMap;
    }

    public JsonMap add(String key, Object value) {
        put(key, value);
        return this;
    }
}
