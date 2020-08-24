package com.dtomics.reflections.utils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.dtomics.reflections.ReflectionConstants.*;

public final class ReflectionUtils {

    public static String getFieldSignature(Class<?> cls, Field field) {
        return getFieldSignature(ClassUtils.index(cls), field);
    }

    public static String getFieldSignature(String index, Field field) {
        return String.format(FIELD_FORMAT, index, field.getName());
    }

    public static String getMethodSignature(Class<?> cls, Method method) {
        return getMethodSignature(ClassUtils.index(cls), method);
    }

    public static String getMethodSignature(String index, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return String.format(
                METHOD_FORMAT,
                index,
                method.getName(),
                Arrays.stream(parameterTypes)
                        .map(Class::getCanonicalName)
                        .collect(Collectors.joining(PARAMETER_DELIMITER))
        );
    }

    public static String getConstructorSignature(Class<?> cls, Constructor<?> constructor) {
        return getConstructorSignature(ClassUtils.index(cls),constructor);
    }

    public static String getConstructorSignature(String index, Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return String.format(
                CONSTRUCTOR_FORMAT,
                index,
                Arrays.stream(parameterTypes)
                        .map(Class::getCanonicalName)
                        .collect(Collectors.joining(PARAMETER_DELIMITER))
        );
    }

    public static String getExecutableSignature(Class<?> cls, Executable executable) {
        return getExecutableSignature(ClassUtils.index(cls), executable);
    }

    public static String getExecutableSignature(String index, Executable executable) {
        return executable instanceof Method ? getMethodSignature(index, (Method) executable) : getConstructorSignature(index, (Constructor<?>) executable);
    }

    public static String getExecutableParamSignature(Class<?> cls, Executable executable, Parameter parameter) {
        return getExecutableParamSignature(getExecutableSignature(cls, executable), parameter);
    }

    public static String getExecutableParamSignature(String signature, Parameter parameter) {
        return String.format(EXECUTABLE_PARAMETER_FORMAT, signature, parameter.getName(), parameter.getType());
    }
}
