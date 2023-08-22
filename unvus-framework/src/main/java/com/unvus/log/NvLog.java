package com.unvus.log;

import com.unvus.log.displayer.DefaultFormatter;
import com.unvus.log.displayer.LogFormatter;
import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NvLog {

    LogLevel value() default LogLevel.INFO;

    String name() default "";

    ChronoUnit unit() default ChronoUnit.SECONDS;

    boolean showArgs() default false;

    boolean showResult() default false;

    boolean showExecutionTime() default true;

    Class<? extends LogFormatter> formatter() default DefaultFormatter.class;
}
