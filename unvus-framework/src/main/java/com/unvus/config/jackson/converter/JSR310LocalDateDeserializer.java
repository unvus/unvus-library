package com.unvus.config.jackson.converter;

import com.unvus.util.DateTools;

import java.io.IOException;
import java.time.LocalDate;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Jackson deserializer for transforming a JSON object (using the ISO 8601 date formatwith optional time)
 * to a JSR310 LocalDate object.
 */
public class JSR310LocalDateDeserializer extends JsonDeserializer<LocalDate> implements ContextualDeserializer {
	private final Logger log = LoggerFactory.getLogger(JSR310LocalDateDeserializer.class);


    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateTimeFormatter instanceFormatter = null;

    public static final JSR310LocalDateDeserializer INSTANCE = new JSR310LocalDateDeserializer();

    public JSR310LocalDateDeserializer() {

    }

    public JSR310LocalDateDeserializer(DateTimeFormatter formatter) {
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
                JSR310LocalDateDeserializer deserializer = new JSR310LocalDateDeserializer();
                deserializer.setInstanceFormatter(DateTimeFormatter.ofPattern(jsonFormat.pattern()));
                return deserializer;
            }
        }
        return new JSR310LocalDateDeserializer(JSR310LocalDateDeserializer.formatter);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
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

                if(parser.nextToken() != JsonToken.END_ARRAY) {
                    throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
                }
                return LocalDate.of(year, month, day);

            case VALUE_STRING:
                if(parser.getCurrentToken().isNumeric()) {
//                    ZonedDateTime.ofInstant(
//                            Instant.ofEpochMilli(node.get("timeStamp").asLong()),
//                            ZoneId.systemDefault()
//                    )
                    return DateTools.convert(parser.getLongValue(), DateTools.ConvertTo.LOCAL_DATE);
                }else {
                    String string = parser.getText().trim();
                    if(string.length() == 0) {
                        return null;
                    }
                    if(instanceFormatter != null) {
                        return LocalDate.parse(string, instanceFormatter);
                    }else {
                        return LocalDate.parse(string, formatter);
                    }
                }
            case VALUE_NUMBER_INT:
                return DateTools.convert(parser.getLongValue(), DateTools.ConvertTo.LOCAL_DATE);
        }
        throw context.wrongTokenException(parser, JsonToken.START_ARRAY, "Expected array or string.");
    }
}
