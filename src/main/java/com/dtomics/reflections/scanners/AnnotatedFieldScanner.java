package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;
import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotatedFieldScanner implements Scanner{
    @Override
    public void onScan(Class<?> type, Cache cache) {
        Field[] fields = type.getDeclaredFields();
        for(Field field : fields) {
            String fieldSignature = ReflectionUtils.getFieldSignature(type, field);
            Annotation[] annotations = field.getAnnotations();
            for(Annotation annotation : annotations)
                cache.put(AnnotatedFieldScanner.class, ClassUtils.index(annotation.annotationType()), fieldSignature);
        }
    }
}
