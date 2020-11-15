package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;
import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.lang.reflect.Field;

public class FieldScanner implements Scanner {

    @Override
    public void onScan(Class<?> type, Cache cache) {
        Field[] fields = type.getDeclaredFields();
        if (fields.length != 0) {
            for (Field field : fields) {
                String fieldSignature = ReflectionUtils.getFieldSignature(type, field);
                cache.put(this.getClass(), ClassUtils.index(type), fieldSignature);
            }
        }
    }
}
