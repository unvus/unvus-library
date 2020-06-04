package com.unvus.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Aspect
@Component
public class RedisSessionAop {
    @Inject
    RedisTemplate<Object, Object> redisTemplate;

    private String namespace;

    public RedisSessionAop(@Value("${unvus.app.name}") String namespace) {
        this.namespace = namespace;
    }

    @Pointcut("execution(void org.springframework.session.data.redis.RedisOperationsSessionRepository.onMessage(..))")
    public void redisSessionOnMessage() {}

    @Around("redisSessionOnMessage()")
    public Object aroundOnMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj;

        try {
            obj = joinPoint.proceed();
        } catch(SerializationException e) {

            Message message = (Message)joinPoint.getArgs()[0];
            byte[] messageBody = message.getBody();
            String body = new String(messageBody);
            int beginIndex = body.lastIndexOf(":") + 1;
            int endIndex = body.length();
            String sessionId = body.substring(beginIndex, endIndex);

            redisTemplate.delete("spring:session:" + namespace + ":" + sessionId);
            return null;
        }

        return obj;
    }
}
