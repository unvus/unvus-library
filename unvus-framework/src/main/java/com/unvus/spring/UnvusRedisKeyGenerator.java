package com.unvus.spring;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class UnvusRedisKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object o, Method method, Object... objects) {
//        StringBuilder sb = new StringBuilder();
////        sb.append(o.getClass().getName());
////        sb.append(method.getName());
//        for (Object obj : objects) {
//            sb.append(obj == null?"null":obj.toString());
//            sb.append(":");
//        }
        return SimpleObjectToStringUtil.convert(objects);
    }
}
