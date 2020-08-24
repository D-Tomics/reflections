package com.dtomics.reflections.utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static List<Class<?>> forName(List<String> values, ClassLoader classLoader) {
        return values.stream().map(s -> forName(s, classLoader)).collect(Collectors.toList());
    }

    public static String index(Class<?> cls) {
        if(cls == null)
            throw new IllegalArgumentException("Cannot index null");
        return cls.getCanonicalName();
    }

    private static Class<?> resolvePrimitiveTypes(String className) {
        Class<?> result = null;
        if (className != null && primitiveTypeMap.containsKey(className))
            result = primitiveTypeMap.get(className);
        return result;
    }
}
