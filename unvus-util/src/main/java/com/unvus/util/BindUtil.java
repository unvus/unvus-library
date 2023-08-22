package com.unvus.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BindUtil {

    private BindUtil() {}

    public static void bind(List<?> sourceList, String sourceProp, List<?> injectList, String injectProp) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        if(CollectionUtils.isEmpty(sourceList) || CollectionUtils.isEmpty(injectList)) {
            return;
        }
        boolean isArraySourceProp = false;
        Class<?> klass = sourceList.get(0).getClass();

        PropertyDescriptor sourcePd = BindUtil.getPropertyDescriptor(klass, sourceProp);
        if(sourcePd == null) {
            throw new IllegalArgumentException("no sourceProp for " + sourceProp);
        }
        if(java.util.List.class.isAssignableFrom(sourcePd.getPropertyType())) {
            isArraySourceProp = true;
        }

        Map<Long, Object> idMap = BindUtil.toIdMap(sourceList);

        for(Object obj : injectList) {
            Long key = null;
            try {
                key = Long.parseLong(BeanUtils.getProperty(obj, injectProp));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            Object source = idMap.get(key);


            if(isArraySourceProp) {
                Object injectObj = sourcePd.getReadMethod().invoke(source);
                if(injectObj == null) {
                    injectObj = new ArrayList<>();
                    sourcePd.getWriteMethod().invoke(source, injectObj);
                }
                ((List)injectObj).add(obj);
            }else {
                sourcePd.getWriteMethod().invoke(source, obj);
            }

        }
    }

    public static Map<Long, Object> toIdMap(List<?> sourceList) {
        return sourceList.stream()
            .collect(
                Collectors.toMap(
                    BindUtil::extractId,
                    Function.identity()
                )
            );
    }



    public static List<Long> toIdList(List<?> sourceList) {
        return sourceList.stream()
            .map(BindUtil::extractId)
            .collect(Collectors.toList());
    }

    public static List<Long> toIdList(List<?> sourceList, String prop) {
        return sourceList.stream()
            .map(x -> extractId(x, prop))
            .collect(Collectors.toList());
    }

    public static List<String> toPropList(List<?> sourceList, String prop) {

        return sourceList.stream()
            .map(x -> extractProp(x, prop))
            .collect(Collectors.toList());
    }

    private static Long extractId(Object x) {
        return extractId(x, "id");
    }

    private static Long extractId(Object x, String prop) {
        Long id = null;
        try {
            id = Long.parseLong(BeanUtils.getProperty(x,prop));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return id;
    }

    private static String extractProp(Object x, String prop) {
        String propValue = null;
        try {
            propValue = BeanUtils.getProperty(x,prop);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return propValue;
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> klass, String sourceProp) throws IntrospectionException {
        PropertyDescriptor sourcePd = null;
        for (PropertyDescriptor pd : Introspector.getBeanInfo(klass).getPropertyDescriptors()) {

            if (sourceProp.equals(pd.getName())) {
                sourcePd = pd;
                break;
            }
        }
        return sourcePd;
    }


}
