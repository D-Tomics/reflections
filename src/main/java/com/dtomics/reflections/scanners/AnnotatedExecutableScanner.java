package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;
import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotatedExecutableScanner implements Scanner {

    @Override
    public void onScan(Class<?> type, Cache cache) {
        Executable[] methods = type.getDeclaredMethods();
        Executable[] constructors = type.getDeclaredConstructors();

        List<Executable> executables = new ArrayList<>();
        executables.addAll(Arrays.asList(methods));
        executables.addAll(Arrays.asList(constructors));

        for(Executable executable : executables) {
            Annotation[] annotations = executable.getDeclaredAnnotations();
            for(Annotation annotation : annotations) {
                String index = ClassUtils.index(annotation.annotationType());
                cache.put(AnnotatedExecutableScanner.class, index, ReflectionUtils.getExecutableSignature(index, executable));
            }
        }
    }
}
