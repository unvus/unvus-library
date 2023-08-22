package com.unvus.config.converter;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by guava on 16.8.4.
 */
@Component
public class StringToMapConverter implements Converter<String, Map> {

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public Map convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        Map queryMap = null;
        try {
            queryMap = objectMapper.readValue(source, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return queryMap;
    }
}
