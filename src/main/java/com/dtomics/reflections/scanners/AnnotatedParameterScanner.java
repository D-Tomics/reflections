package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;
import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotatedParameterScanner implements Scanner{

    @Override
    public void onScan(Class<?> type, Cache cache) {
        Executable[] methods = type.getDeclaredMethods();
        Executable[] constructors = type.getDeclaredConstructors();

        List<Executable> executables = new ArrayList<>();
        executables.addAll(Arrays.asList(methods));
        executables.addAll(Arrays.asList(constructors));

        for(Executable executable : executables) {
            Parameter[] parameters = executable.getParameters();
            int index = 0;
            for(Parameter parameter : parameters) {
                Annotation[] annotations = parameter.getAnnotations();
                for(Annotation annotation : annotations) {
                    cache.put(
                            AnnotatedParameterScanner.class,
                            ClassUtils.index(annotation.annotationType()),
                            ReflectionUtils.getExecutableParamSignature(type, executable, parameter, index)
                    );
                }
                index++;
            }
        }
    }
}
