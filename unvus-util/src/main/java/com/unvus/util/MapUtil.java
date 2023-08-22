package com.unvus.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map splitKey(Map<String, ?> param) {
        Map result = new HashMap();
        for(Map.Entry<String, ?> entry: param.entrySet()) {
            String[] keyArr = StringUtils.split(entry.getKey(), '.');
            recursivePut(result, entry.getValue(), keyArr, 0);
        }
        return result;
    }

    private static void recursivePut(Map<String, Object> result, Object value, String[] keyArr, int idx) {
        if(keyArr.length == (idx + 1)) {
            result.put(keyArr[idx], value);
            return;
        }else {
            if(!result.containsKey(keyArr[idx])) {
                result.put(keyArr[idx], new HashMap());
            }
            recursivePut((Map)result.get(keyArr[idx]), value, keyArr, ++idx);
        }
    }
}
