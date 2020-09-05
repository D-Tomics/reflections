package com.dtomics.reflections.utils;

import com.dtomics.reflections.exceptions.InvalidSignatureException;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import static com.dtomics.reflections.ReflectionConstants.*;

public final class ReflectionUtils {

    public static List<Class<?>> getParameterTypesFromSignature(String signature, Class<?> cls, ClassLoader classLoader) {
        String[] paramTypeNames = extractExecutableParamTypes(signature);
        return ClassUtils.forName(Arrays.asList(paramTypeNames), classLoader);
    }

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
        return String.format(METHOD_FORMAT, index, method.getName(), getParameterTypesSignature(method));
    }

    public static String getConstructorSignature(Class<?> cls, Constructor<?> constructor) {
        return getConstructorSignature(ClassUtils.index(cls),constructor);
    }

    public static String getConstructorSignature(String index, Constructor<?> constructor) {
        return String.format(CONSTRUCTOR_FORMAT, index, getParameterTypesSignature(constructor));
    }

    public static String getExecutableSignature(Class<?> cls, Executable executable) {
        return getExecutableSignature(ClassUtils.index(cls), executable);
    }

    public static String getExecutableSignature(String index, Executable executable) {
        return executable instanceof Method ? getMethodSignature(index, (Method) executable) : getConstructorSignature(index, (Constructor<?>) executable);
    }

    public static String getExecutableParamSignature(Class<?> cls, Executable executable, Parameter parameter, int index) {
        return getExecutableParamSignature(getExecutableSignature(cls, executable), parameter, index);
    }

    public static String getExecutableParamSignature(String signature, Parameter parameter, int index) {
        return String.format(EXECUTABLE_PARAMETER_FORMAT, signature, ClassUtils.index(parameter.getType()),index);
    }

    public static boolean isMethod(String signature) {
        return isValidMethodSignature(signature);
    }

    public static boolean isConstructor(String signature) {
        return isValidConstructorSignature(signature);
    }

    public static String extractClassName(String signature) {
        if(isValidSignature(signature)) {
            int index = signature.indexOf(SEPARATOR_START);
            return signature.substring(0, index);
        }
        throw new InvalidSignatureException(signature,"class");
    }

    public static String extractFieldName(String signature) {
        if(isValidFieldSignature(signature)) {
            int index = signature.indexOf(FIELD_SEPARATOR) + FIELD_SEPARATOR.length();
            return signature.substring(index);
        }
        throw new InvalidSignatureException(signature,"field");
    }

    public static String extractMethodName(String signature) {
        if(isValidMethodSignature(signature)) {
            int index = signature.indexOf(METHOD_SEPARATOR) + METHOD_SEPARATOR.length();
            return signature.substring(index, signature.indexOf(EXECUTABLE_PARAM_START));
        }
        throw new InvalidSignatureException(signature,"method");
    }

    public static String[] extractExecutableParamTypes(String signature) {
        if(isValidMethodSignature(signature) ||
                isValidConstructorSignature(signature)) {
            int index = signature.indexOf(EXECUTABLE_PARAM_START) + EXECUTABLE_PARAM_START.length();
            String parameterString = signature.substring(index,signature.indexOf(EXECUTABLE_PARAM_END));
            String[] pTypes = parameterString.split(PARAMETER_DELIMITER);
            for(int i = 0; i < pTypes.length; i++)
                pTypes[i] = pTypes[i].split(" ")[0];
            return pTypes;
        }
        throw new InvalidSignatureException(signature,"parameter");
    }

    private static boolean isValidMethodSignature(String signature) {
        return signature.contains(METHOD_SEPARATOR);
    }

    private static boolean isValidConstructorSignature(String signature) {
        return signature.contains(CONSTRUCTOR_SEPARATOR);
    }

    private static boolean isValidFieldSignature(String signature) {
        return signature.contains(FIELD_SEPARATOR);
    }

    private static boolean isValidParamSignature(String signature) {
        return signature.contains(PARAMETER_SEPARATOR);
    }

    private static boolean isValidSignature(String signature) {
        return isValidMethodSignature(signature) ||
                isValidConstructorSignature(signature) ||
                isValidFieldSignature(signature) ||
                isValidParamSignature(signature);
    }

    private static String getParameterTypesSignature(Executable executable) {
        Class<?>[] paramTypes = executable.getParameterTypes();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < paramTypes.length; i++) {
            stringBuilder.append(ClassUtils.index(paramTypes[i])).append(" ").append(i).append(PARAMETER_DELIMITER);
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

}
