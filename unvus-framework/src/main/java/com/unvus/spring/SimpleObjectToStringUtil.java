package com.unvus.spring;

import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.codec.digest.DigestUtils;

public class SimpleObjectToStringUtil {

    public static String convert(Object o) {

        if(o instanceof String) {
            return (String)o;
        }
        return toStringRecursive(o, new StringBuffer());
    }

    private static String toStringRecursive(Object obj, StringBuffer sb) {
        if(obj != null) {
            if(obj instanceof Collection) {
                sb.append("[");
                for (Object item : (Collection) obj) {
                    toStringRecursive(item, sb);
                }
                sb.append("]");
                sb.append(":");
            }else if(obj.getClass().isArray()) {
                int length = Array.getLength(obj);
                sb.append("[");
                for (int i = 0; i < length; i ++) {
                    toStringRecursive(Array.get(obj, i), sb);
                }
                sb.append("]");
                sb.append(":");
            }else {
                String tmp = obj.toString();
                if("".equalsIgnoreCase(tmp)) {
                    sb.append("_empty");
                }else if(tmp.length() > 60) {
                    sb.append(DigestUtils.sha256Hex(tmp));
                }else {
                    sb.append(tmp);
                }
                sb.append(":");
            }
        }else {
            sb.append("_null");
            sb.append(":");
        }
        return sb.toString();
    }
}
