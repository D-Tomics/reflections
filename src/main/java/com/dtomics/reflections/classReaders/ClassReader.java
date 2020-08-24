package com.dtomics.reflections.classReaders;

import java.net.URL;
import java.util.List;

public interface ClassReader {
    List<Class<?>> read(URL url, String packageName, ClassLoader classLoader);
}
