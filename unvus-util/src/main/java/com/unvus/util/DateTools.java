package com.unvus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guava on 3/17/16.
 */
public class DateTools extends org.apache.commons.lang3.time.DateUtils {

    private static final Logger log = LoggerFactory.getLogger(DateTools.class);

    public enum ConvertTo {
        LONG(java.lang.Long.class),
        DATE(java.util.Date.class),
        LOCAL_DATE(java.time.LocalDate.class),
        LOCAL_DATE_TIME(java.time.LocalDateTime.class),
        ZONED_DATE_TIME(java.time.ZonedDateTime.class);

        private final Class<?> type;

        ConvertTo(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }
    }

    private DateTools() {
    }

    public static <T> T now(ConvertTo convertTo) {
        Class<T> type = (Class<T>) convertTo.getType();

        if (type.equals(ZonedDateTime.class)) {
            // ZonedDateTime TO ZonedDateTime
            return type.cast(ZonedDateTime.now());

        } else if (type.equals(LocalDateTime.class)) {
            // ZonedDateTime TO LocalDate
            return type.cast(LocalDateTime.now());

        } else if (type.equals(LocalDate.class)) {
            // ZonedDateTime TO LocalDate
            return type.cast(LocalDate.now());

        } else if (type.equals(Long.class)) {
            // ZonedDateTime TO Long
            return type.cast(System.currentTimeMillis());
        }

        throw new UnsupportedOperationException();
    }


    public static <T> T convert(ZonedDateTime zonedDateTime, ConvertTo convertTo) {
        Class<T> type = (Class<T>) convertTo.getType();

        if (type.equals(ZonedDateTime.class)) {
            // ZonedDateTime TO ZonedDateTime
            return type.cast(zonedDateTime);

        } else if (type.equals(LocalDateTime.class)) {
            // ZonedDateTime TO LocalDateTime
            return type.cast(convertZonedDateTimeToLocalDateTime(zonedDateTime));

        } else if (type.equals(LocalDate.class)) {
            // ZonedDateTime TO LocalDate
            return type.cast(convertZonedDateTimeToLocalDate(zonedDateTime));

        } else if (type.equals(Date.class)) {
            // ZonedDateTime TO Date
            return type.cast(convertZonedDateTimeToDate(zonedDateTime));
        } else if (type.equals(Long.class)) {
            // ZonedDateTime TO Long
            return type.cast(convertZonedDateTimeToLong(zonedDateTime));
        }

        throw new UnsupportedOperationException();
    }

    public static <T> T convert(LocalDateTime localDateTime, ConvertTo convertTo) {
        Class<T> type = (Class<T>) convertTo.getType();

        if (type.equals(LocalDateTime.class)) {
            // LocalDateTime TO ZonedDateTime
            return type.cast(localDateTime);

        } else if (type.equals(ZonedDateTime.class)) {
            // LocalDateTime TO LocalDateTime
            return type.cast(convertLocalDateTimeToZonedDateTime(localDateTime));

        } else if (type.equals(LocalDate.class)) {
            // LocalDateTime TO LocalDate
            return type.cast(convertLocalDateTimeToLocalDate(localDateTime));

        } else if (type.equals(Date.class)) {
            // LocalDateTime TO Date
            return type.cast(convertLocalDateTimeToDate(localDateTime));
        } else if (type.equals(Long.class)) {
            // LocalDateTime TO Long
            return type.cast(convertLocalDateTimeToLong(localDateTime));
        }

        throw new UnsupportedOperationException();
    }

    public static <T> T convert(LocalDate localDate, ConvertTo convertTo) {
        Class<T> type = (Class<T>) convertTo.getType();

        if (type.equals(LocalDate.class)) {
            // LocalDate TO LocalDate
            return type.cast(localDate);
        } else if (type.equals(LocalDateTime.class)) {
            // LocalDate TO LocalDateTime
            return type.cast(convertLocalDateToLocalDateTime(localDate));

        } else if (type.equals(ZonedDateTime.class)) {
            // LocalDate TO ZonedDateTime
            return type.cast(convertLocalDateToZonedDateTime(localDate));

        } else if (type.equals(Date.class)) {
            // LocalDate TO Date
            return type.cast(convertLocalDateToDate(localDate));
        } else if (type.equals(Long.class)) {
            // LocalDate TO Long
            return type.cast(convertLocalDateToLong(localDate));
        }

        throw new UnsupportedOperationException();
    }

    public static <T> T convert(Long timemillis, ConvertTo convertTo) {
        Class<T> type = (Class<T>) convertTo.getType();

        if (type.equals(Long.class)) {
            // Long TO Long
            return type.cast(timemillis);

        } else if (type.equals(LocalDateTime.class)) {
            // Long TO LocalDateTime
            return type.cast(convertLongToLocalDateTime(timemillis));

        } else if (type.equals(LocalDate.class)) {
            // Long TO LocalDate
            return type.cast(convertLongToLocalDate(timemillis));

        } else if (type.equals(ZonedDateTime.class)) {
            // Long TO ZonedDateTime
            return type.cast(convertLongToZonedDateTime(timemillis));

        } else if (type.equals(Date.class)) {
            // Long TO Date
            return type.cast(convertLongToZonedDate(timemillis));
        }

        throw new UnsupportedOperationException();
    }

    public static String format(ZonedDateTime zonedDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }

    public static String format(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    public static String format(LocalDate localDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDate.format(formatter);
    }

    public static String format(Date date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String format(Long timemillis, String format) {
        Date date = new Date(timemillis);
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String format(String format) {
        Calendar cal = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(cal.getTime());
    }

    public static <T> T convertFromFormattedStr(String dateStr, String format, ConvertTo convertTo) {
        DateFormat formatter = new SimpleDateFormat(format);

        try {
            Date date = formatter.parse(dateStr);
            return convert(date.getTime(), convertTo);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static ZonedDateTime convertLongToZonedDateTime(Long timemillis) {
        return ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timemillis),
            ZoneId.systemDefault()
        );
    }

    private static Date convertLongToZonedDate(Long timemillis) {
        return new Date(timemillis);
    }

    private static Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    private static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static Long convertZonedDateTimeToLong(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    private static Long convertLocalDateTimeToLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private static LocalDate convertZonedDateTimeToLocalDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDate();
    }

    private static LocalDate convertLocalDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    private static LocalDateTime convertZonedDateTimeToLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDateTime();
    }

    private static ZonedDateTime convertLocalDateTimeToZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault());
    }


    private static Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    private static Long convertLocalDateToLong(LocalDate localDate) {
        return localDate.toEpochDay();
    }

    private static ZonedDateTime convertLocalDateToZonedDateTime(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.UTC);
    }

    private static LocalDateTime convertLocalDateToLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    private static LocalDateTime convertLongToLocalDateTime(Long timemillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timemillis), ZoneId.systemDefault());
    }

    private static LocalDate convertLongToLocalDate(Long timemillis) {
        return Instant.ofEpochMilli(timemillis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate asLocalDate(Calendar cal) {
        return asLocalDate(cal.getTime());
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate min(LocalDate a, LocalDate b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.isAfter(b)) {
            return b;
        } else {
            return a;
        }
    }

    public static LocalDate max(LocalDate a, LocalDate b) {
        if (a == null) return b;
        if (b == null) return a;

        if (a.isAfter(b)) {
            return a;
        } else {
            return b;
        }
    }

    public static boolean isSameDay(final LocalDate date1, final LocalDate date2) {
        return isSameDay(asDate(date1), asDate(date2));
    }

}
