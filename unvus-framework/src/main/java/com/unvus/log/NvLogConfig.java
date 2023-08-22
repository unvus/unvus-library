package com.unvus.log;

import com.unvus.log.displayer.DefaultFormatter;
import com.unvus.log.displayer.LogFormatter;
import com.unvus.log.displayer.impl.PlainFormatter;

import java.util.HashMap;
import java.util.Map;

public class NvLogConfig {

    private static Map<Class<? extends LogFormatter>, LogFormatter> instanceMap;

    static {
        instanceMap = new HashMap<>();
    }

    private static Class<? extends LogFormatter> defaultClass = PlainFormatter.class;

    public static void setDefaultFormatter(Class<? extends LogFormatter> defaultClass) {
        assert !(DefaultFormatter.class.isAssignableFrom(defaultClass));

        if(DefaultFormatter.class.isAssignableFrom(defaultClass)) {
            throw new IllegalArgumentException();
        }

        NvLogConfig.defaultClass = defaultClass;
    }

    protected static LogFormatter getFormatter(Class<? extends LogFormatter> klass) {
        if(!instanceMap.containsKey(klass)) {
            try {
                instanceMap.put(klass, klass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instanceMap.get(klass);
    }

    public static LogFormatter getFormatter() {
        return getFormatter(defaultClass);
    }
}
