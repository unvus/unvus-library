package com.unvus.config.mybatis.interceptor;

import com.unvus.domain.UnvusSecureUser;
import com.unvus.domain.audit.AbstractAuditingEntity;
import com.unvus.domain.audit.AbstractAuditingImmutableEntity;
import com.unvus.spring.SecurityUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
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
public class AuditingInterceptor implements Interceptor {

    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
        Object params = args[PARAMETER_INDEX];

        SqlCommandType commandType = ms.getSqlCommandType();

        if(commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UPDATE) {
            if(params instanceof AbstractAuditingImmutableEntity) {
                AbstractAuditingImmutableEntity entity = (AbstractAuditingImmutableEntity)params;
                if(entity.getCreatedBy() == null) {
                    entity.setCreatedBy(getCurrentMemberId());
                }
                if(entity.getCreatedDt() == null) {
                    entity.setCreatedDt(LocalDateTime.now());
                }
                if(params instanceof AbstractAuditingEntity) {
                    AbstractAuditingEntity auditEntity = (AbstractAuditingEntity)entity;
                    if(auditEntity.getModifiedBy() == null) {
                        auditEntity.setModifiedBy(getCurrentMemberId());
                    }
                    if(auditEntity.getModifiedDt() == null) {
                        auditEntity.setModifiedDt(LocalDateTime.now());
                    }
                }
            }else if(params instanceof ParamMap) {
                ((ParamMap)params).put("createdBy", getCurrentMemberId());
                ((ParamMap)params).put("createdDt", ZonedDateTime.now());
                ((ParamMap)params).put("modifiedBy", getCurrentMemberId());
                ((ParamMap)params).put("modifiedDt", ZonedDateTime.now());
            } else if (params instanceof Map) {
                ((HashMap)params).put("createdBy", getCurrentMemberId());
                ((HashMap)params).put("createdDt", ZonedDateTime.now());
                ((HashMap)params).put("modifiedBy", getCurrentMemberId());
                ((HashMap)params).put("modifiedDt", ZonedDateTime.now());
            }
        }else {
            Long currentUserId = getCurrentMemberId();
            if(params instanceof ParamMap) {
                ((ParamMap)params).put("currentUserId", currentUserId.compareTo(0L)>0?currentUserId:null);
                ((ParamMap)params).put("projections", null);
                ((ParamMap)params).put("joinProjections", null);

                if (!((ParamMap)params).containsKey("customJoined")) {
                    ((ParamMap)params).put("customJoined", null);
                }
            } else if (params instanceof Map) {
                ((HashMap)params).put("currentUserId", currentUserId.compareTo(0L)>0?currentUserId:null);
                ((HashMap)params).put("projections", null);
                ((HashMap)params).put("joinProjections", null);

                if (!((HashMap)params).containsKey("customJoined")) {
                    ((HashMap)params).put("customJoined", null);
                }
            }
        }

        Object result = invocation.proceed();

        return result;
    }

    private Long getCurrentMemberId() {
        Long id = -1L;
        UnvusSecureUser user = (UnvusSecureUser) SecurityUtils.getCurrentUser();
        if(user != null) {
            id = user.getId();
        }
        return id;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
