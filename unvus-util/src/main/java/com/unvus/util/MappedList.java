package com.unvus.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
public class MappedList<E> extends ArrayList<E> {

    private Map<Object, E> indexMap = new HashMap<>();
    private transient PropertyDescriptor pd;

    public MappedList(PropertyDescriptor pd) {
        this.pd = pd;
    }

    private void addIndex(E e) throws IllegalAccessException, InvocationTargetException {
        indexMap.put(pd.getReadMethod().invoke(e), e);
    }

    private void removeIndex(Object o) throws IllegalAccessException, InvocationTargetException {

        indexMap.remove(pd.getReadMethod().invoke(o));
    }

    public E get(Object o) {
        return indexMap.get(o);
    }

    @Override
    public boolean add(E e) {
        if(super.add(e)) {
            try {
                addIndex(e);
                return true;
            } catch (IllegalAccessException|InvocationTargetException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(super.addAll(c)) {
            for(E e: c) {
                try {
                    addIndex(e);
                } catch (IllegalAccessException|InvocationTargetException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if(super.remove(o)) {
            try {
                removeIndex(o);
                return true;
            } catch (IllegalAccessException|InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E e = super.remove(index);
        try {
            removeIndex(e);
        } catch (IllegalAccessException|InvocationTargetException e1) {
            log.error(e1.getMessage(), e1);
        }
        return e;
    }

    public static PropertyDescriptor prop(Class<?> klass, String fieldName) {
        PropertyDescriptor pd = null;
        try {
            pd = new PropertyDescriptor(fieldName, klass);
        } catch (IntrospectionException e) {
            log.error(e.getMessage(), e);
        }
        return pd;
    }
}
