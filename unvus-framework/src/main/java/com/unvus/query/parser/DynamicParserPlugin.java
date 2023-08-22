package com.unvus.query.parser;

import com.unvus.query.QueryBuilder;

/**
 * _d.keyword.type
 * _d.keyword.value
 *
 */
public class DynamicParserPlugin implements ParserPlugin {

    public static final String TYPE_KEY = "type";
    public static final String VALUE_KEY = "value";

    private String prefix = "_d";
    private String name = "dynamic";
//    private Map<String, Mapper> mapperMap = new HashMap<>();
//
//    public DynamicParserPlugin(String name, Mapper... mappers) {
//        this.name = name;
//        for (Mapper mapper : mappers) {
//            mapperMap.put(mapper.name, mapper);
//        }
//    }
//
//    public DynamicParserPlugin(String prefix, String name, Mapper... mappers) {
//        this.prefix = prefix;
//        this.name = name;
//        for (Mapper mapper : mappers) {
//            mapperMap.put(mapper.name, mapper);
//        }
//    }
//
//
    @Override
    public void parse(QueryBuilder builder) {
//        Map<String, Object> param = builder.getQueryParam();
//
//        Map<String, Object> prefixMap = (Map<String, Object>)param.get(prefix);
//        if(prefixMap == null) {
//            return;
//        }
//
//        Map<String, Object> itemMap = (Map<String, Object>)prefixMap.get(name);
//        if(itemMap == null) {
//            return;
//        }
//
//        Object value = itemMap.get(VALUE_KEY);
//
//        if(value != null) {
//            String type = (String)itemMap.get(TYPE_KEY);
//
//            if(type != null && mapperMap.containsKey(type)) {
//                Mapper mapper = mapperMap.get(type);
//                builder.and(mapper.field, value, mapper.oper);
//            }else {
//                builder.sub(
//                    qb -> {
//                        for(Map.Entry<String, Mapper> entry: mapperMap.entrySet()) {
//                            Mapper mapper = entry.getValue();
//                            qb.or(mapper.field, value, mapper.oper);
//                        }
//                    }
//                );
//            }
//        }
//
//
////        param.remove(fullPrefix + TYPE_KEY);
////        param.remove(fullPrefix + VALUE_KEY);
    }
//
//    @Getter
//    @Setter
//    public static class Mapper {
//        String name;
//        Field field;
//        Oper oper;
//
//        public Mapper(Field field) {
//            this(field.getProperty(), field);
//        }
//
//        public Mapper(String name, Field field) {
//            this(name, field, Oper.LIKE_FULL);
//        }
//
//        public Mapper(String name, Field field, Oper oper) {
//            this.name = name;
//            this.field = field;
//            this.oper = oper;
//        }
//    }
}
