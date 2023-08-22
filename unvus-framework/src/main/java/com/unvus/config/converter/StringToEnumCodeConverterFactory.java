package com.unvus.config.converter;

import com.unvus.domain.EnumCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by guava on 16.8.10.
 */
@Component
public class StringToEnumCodeConverterFactory implements ConverterFactory<String, EnumCode> {

    private static class StringToEnumCodeConverter<T extends EnumCode> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumCodeConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }

            Object o = null;
            try {
                Method method = enumType.getMethod("getByCode", String.class);
                o = method.invoke(null, source.trim());
            } catch (Exception ignore) {}
            return (T)o;
        }
    }

    @Override
    public <T extends EnumCode> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumCodeConverter(targetType);
    }

}
