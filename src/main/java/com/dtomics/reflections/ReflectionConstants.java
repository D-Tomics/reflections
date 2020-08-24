package com.dtomics.reflections;

public final class ReflectionConstants {
    public static final String
            ARRAY_SUFFIX = "[]",
            CLASS_FILE_SUFFIX = ".class",
            PARAMETER_DELIMITER = ",",
            EXECUTABLE_PARAM_END = ")",
            EXECUTABLE_PARAM_START = "(",
            SEPARATOR_START = "<",
            SEPARATOR_END = ">",
            FIELD_SEPARATOR = "<field>",
            METHOD_SEPARATOR = "<method>",
            PARAMETER_SEPARATOR = "<param>",
            CONSTRUCTOR_SEPARATOR = "<init>",
            FIELD_FORMAT = "%s" + FIELD_SEPARATOR + "%s", // class name - field name
            METHOD_FORMAT = "%s" + METHOD_SEPARATOR + "%s(%s)",  // class name - method name - method params
            CONSTRUCTOR_FORMAT = "%s" + CONSTRUCTOR_SEPARATOR + "(%s)", // class name - constructor params
            EXECUTABLE_PARAMETER_FORMAT = "%s"+PARAMETER_SEPARATOR+"%s<%s>"; // index - param name - param type
            // index could be a method signature or a constructor signature
}
