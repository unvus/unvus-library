package com.unvus.config.converter;

import com.unvus.domain.EnumCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by guava on 16.8.10.
 */
@Component
public class EnumCodeToStringConverter implements Converter<EnumCode, String> {

    @Override
    public String convert(EnumCode source) {
        if (source == null) {
            return null;
        }

        return source.getCode();
    }
}
