package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;
import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotatedClassFieldScanner implements Scanner {


    private final Class<? extends Annotation> annotationClass;

    public AnnotatedClassFieldScanner(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public void onScan(Class<?> type, Cache cache) {
        if(type.isAnnotationPresent(annotationClass)) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                String fieldSignature = ReflectionUtils.getFieldSignature(type, field);
                cache.put(this.getClass(), ClassUtils.index(type), fieldSignature);
            }
        }
    }
}
