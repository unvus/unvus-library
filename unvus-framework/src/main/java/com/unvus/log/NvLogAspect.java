package com.unvus.log;

import com.unvus.log.displayer.LogFormatter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Aspect
@Component
public class NvLogAspect {

    @Around("@annotation(com.unvus.log.NvLog) || (execution(public * *(..)) && within(@NvLog *))")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        CodeSignature codeSignature = (CodeSignature) point.getSignature();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        NvLog annotation = method.getAnnotation(NvLog.class);
        if(annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(NvLog.class);
        }
        Logger logger;
        if(annotation.name() != null && !"".equals(annotation.name().trim())) {
            logger = LoggerFactory.getLogger(annotation.name());
        }else {
            logger = LoggerFactory.getLogger(method.getDeclaringClass());
        }

        LogLevel level = annotation.value();
        ChronoUnit unit = annotation.unit();
        boolean showArgs = annotation.showArgs();
        boolean showResult = annotation.showResult();
        boolean showExecutionTime = annotation.showExecutionTime();
        LogFormatter formatter = NvLogConfig.getFormatter(annotation.formatter());

        String methodName = method.getName();
        Object[] methodArgs = point.getArgs();
        String[] methodParams = codeSignature.getParameterNames();

        log(logger, level, formatter.entry(methodName, showArgs, methodParams, methodArgs));

        Instant start = Instant.now();
        Object response = point.proceed();
        Instant end = Instant.now();

        log(logger, level, formatter.exit(methodName, showResult, response, showExecutionTime, unit, start, end));

        return response;
    }

    static void log(Logger logger, LogLevel level, String message) {
        switch (level) {
            case DEBUG:
                logger.debug(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
            case FATAL:
                logger.error(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }
}
