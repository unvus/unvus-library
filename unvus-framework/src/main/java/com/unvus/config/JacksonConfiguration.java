package com.unvus.config;

import com.unvus.config.jackson.converter.JSR310DateTimeSerializer;
import com.unvus.config.jackson.converter.JSR310LocalDateDeserializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@Configuration
public class JacksonConfiguration {

    @Value("${unvus.format.date-format:yyyy.MM.dd}")
    private String dateFormat;

    @Value("${unvus.format.datetime-format:yyyy.MM.dd HH:mm:ss}")
    private String datetimeFormat;

    @Inject
    private Environment env;


    /**
     * Support for Java date and time API.
     * @return the corresponding Jackson module.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(OffsetDateTime.class, JSR310DateTimeSerializer.INSTANCE);
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern(datetimeFormat)));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(datetimeFormat)));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
        module.addSerializer(Instant.class, JSR310DateTimeSerializer.INSTANCE);
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(datetimeFormat)));
        module.addDeserializer(LocalDate.class, new JSR310LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));

        return module;
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    /*
     * Jackson Afterburner module to speed up serialization/deserialization.
     */
    @Bean
    public AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

    /*
     * Module for serialization/deserialization of RFC7807 Problem.
     */
    @Bean
    ProblemModule problemModule() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());

        if (activeProfiles.contains(UnvusConstants.SPRING_PROFILE_PRODUCTION)) {
            return new ProblemModule();
        }else {
            return new ProblemModule().withStackTraces();
        }
    }

    /*
     * Module for serialization/deserialization of ConstraintViolationProblem.
     */
    @Bean
    ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

//    @Primary
    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {

        return new Jackson2ObjectMapperBuilder()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .featuresToEnable(
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            .modules(javaTimeModule(), jdk8TimeModule(), afterburnerModule(), problemModule(), constraintViolationProblemModule());
    }

//    @Primary
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperBuilder() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .featuresToEnable(
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .modules(javaTimeModule(), jdk8TimeModule(), afterburnerModule(), problemModule(), constraintViolationProblemModule());
        };
    }

}
