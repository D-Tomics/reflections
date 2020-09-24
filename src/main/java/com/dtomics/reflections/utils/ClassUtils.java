package com.dtomics.reflections.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.dtomics.reflections.ReflectionConstants.ARRAY_SUFFIX;

public final class ClassUtils {

    private static Map<String, Class<?>> primitiveTypeMap = new HashMap<>(17) {{
        put("int",              int.class   ); put("int[]",              int[].class   );
        put("char",             char.class  ); put("char[]",             char[].class  );
        put("byte",             byte.class  ); put("byte[]",             byte[].class  );
        put("long",             long.class  ); put("long[]",             long[].class  );
        put("short",            short.class ); put("short[]",            short[].class );
        put("float",            float.class ); put("float[]",            float[].class );
        put("double",           double.class); put("double[]",           double[].class);
        put("java.lang.String", String.class); put("java.lang.String[]", String[].class);

        put("void", void.class);
    }};

    public static Class<?> forName(String canonicalName, ClassLoader classLoader) {
        if(canonicalName == null) return null;
        Class<?> cls = resolvePrimitiveTypes(canonicalName);
        if (cls != null)
            return cls;

        if(canonicalName.contains(ARRAY_SUFFIX)) {
            String className = canonicalName.substring(0, canonicalName.length() - ARRAY_SUFFIX.length());
            Class<?> arrayType = forName(className, classLoader);
            return Array.newInstance(arrayType,0).getClass();
        }

        try {
            return classLoader != null ? classLoader.loadClass(canonicalName) : Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found : " + canonicalName);
        }
    }

    public static List<Class<?>> forName(Collection<String> values, ClassLoader classLoader) {
        return values != null ? values.stream().map(s -> forName(s, classLoader)).collect(Collectors.toList()) : null;
    }

    public static String index(Class<?> cls) {
        if(cls == null)
            throw new IllegalArgumentException("Cannot index null");
        return cls.getCanonicalName();
    }

    public static Field getDeclaredField(Class<?> of, String name) {
        Objects.requireNonNull(of,"class of the field["+name+"] cannot be null");
        try {
            return of.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Constructor getDeclaredConstructor(Class<?> of, Class<?>...parameterTypes) {
        Objects.requireNonNull(of, "class of the required constructor cannot be null");
        try {
            return of.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getDeclaredMethod(Class<?> of, String name, Class<?>...parameterTypes) {
        Objects.requireNonNull(of, "class of required method["+name+"] cannot be null");
        try {
            return of.getDeclaredMethod(name,parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Class<?> resolvePrimitiveTypes(String className) {
        Class<?> result = null;
        if (className != null && primitiveTypeMap.containsKey(className))
            result = primitiveTypeMap.get(className);
        return result;
    }
}
