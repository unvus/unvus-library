package com.unvus.config.mybatis.customize;

import com.fasterxml.jackson.databind.JavaType;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;

/**
 * Created by guava on 16.8.17.
 */
public abstract class BaseTypeAwareTypeHandler<T> extends BaseTypeHandler<T> {
    protected Configuration configuration;

    public void setConfiguration(Configuration c) {
        this.configuration = c;
    }

    protected JavaType javaType;

    public void setJavaType(JavaType javaType) {
        this.javaType = javaType;
    }
}
