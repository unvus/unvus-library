package com.unvus.config.jackson.converter;

import com.unvus.util.DateTools;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import org.apache.commons.lang3.StringUtils;

public final class JSR310ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> implements ContextualDeserializer {

    private static DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));
    private DateTimeFormatter formatter = DEFAULT_FORMATTER;

    private DateTimeFormatter instanceFormatter = null;

    public static final JSR310ZonedDateTimeDeserializer INSTANCE = new JSR310ZonedDateTimeDeserializer();

    public JSR310ZonedDateTimeDeserializer() {

    }

    public JSR310ZonedDateTimeDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    protected void setInstanceFormatter(DateTimeFormatter instanceFormatter) {
        this.instanceFormatter = instanceFormatter;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {

        JsonFormat jsonFormat = beanProperty.getAnnotation(JsonFormat.class);

        if(jsonFormat != null) {
            if(StringUtils.isNotBlank(jsonFormat.pattern())) {
                JSR310ZonedDateTimeDeserializer deserializer = new JSR310ZonedDateTimeDeserializer();
                deserializer.setInstanceFormatter(DateTimeFormatter.ofPattern(jsonFormat.pattern()));
                return deserializer;
            }
        }
        return new JSR310ZonedDateTimeDeserializer(this.formatter);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        switch(parser.getCurrentToken()) {
            case START_ARRAY:
                if(parser.nextToken() == JsonToken.END_ARRAY) {
                    return null;
                }
                int year = parser.getIntValue();

                parser.nextToken();
                int month = parser.getIntValue();

                parser.nextToken();
                int day = parser.getIntValue();

                parser.nextToken();
                int hour = parser.getIntValue();

                parser.nextToken();
                int min = parser.getIntValue();

                parser.nextToken();
                int sec = parser.getIntValue();

                if(parser.nextToken() != JsonToken.END_ARRAY) {
                    throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
                }

                return ZonedDateTime.of(LocalDateTime.of(year, month, day, hour, min, sec), ZoneId.systemDefault());

            case VALUE_STRING:
                String string = parser.getText().trim();
                if(string.length() == 0) {
                    return null;
                }
                if(instanceFormatter != null) {
                    return ZonedDateTime.of(LocalDateTime.parse(string, instanceFormatter), ZoneId.systemDefault());
                }else {
                    return ZonedDateTime.of(LocalDateTime.parse(string, formatter), ZoneId.systemDefault());
                }
            case VALUE_NUMBER_INT:
                return DateTools.convert(parser.getLongValue(), DateTools.ConvertTo.ZONED_DATE_TIME);
        }
        throw context.wrongTokenException(parser, JsonToken.START_ARRAY, "Expected array or string.");
    }
}
