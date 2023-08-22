package com.unvus.config.mybatis.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import lombok.extern.slf4j.Slf4j;

@Intercepts({
    @Signature(type = Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method="update", args={MappedStatement.class, Object.class})
})
@Slf4j
public class PreventInjectionInterceptor implements Interceptor {

    private static final int PARAMETER_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        Object params = args[PARAMETER_INDEX];

        if(params instanceof ParamMap) {
            ((ParamMap)params).remove("_alias");
            ((ParamMap)params).remove("_asPrefix");
            ((ParamMap)params).remove("_as_prefix");
        } else if (params instanceof Map) {
            ((HashMap)params).remove("_alias");
            ((HashMap)params).remove("_asPrefix");
            ((HashMap)params).remove("_as_prefix");
        }

        Object result = invocation.proceed();

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
