package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;
import com.dtomics.reflections.utils.ClassUtils;

import java.lang.annotation.Annotation;

public class AnnotatedClassScanner implements Scanner{
    @Override
    public void onScan(final Class<?> type, Cache cache) {
        Annotation[] annotations = type.getAnnotations();
        if(annotations == null) return;
        for(Annotation annotation : annotations)
            cache.put(AnnotatedClassScanner.class, ClassUtils.index(annotation.annotationType()), type.getCanonicalName());
    }
}
