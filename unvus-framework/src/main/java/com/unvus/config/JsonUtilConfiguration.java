package com.unvus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unvus.util.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonUtilConfiguration {
    private final ObjectMapper objectMapper;

    public JsonUtilConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(JsonUtil.class)
    public JsonUtil jsonUtil() {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.setMapper(objectMapper);
        return jsonUtil;
    }
}
