package com.dtomics.reflections.classReaders;

import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.dtomics.reflections.ReflectionConstants.CLASS_FILE_SUFFIX;


public class JarFileReader implements ClassReader {

    @Override
    public List<Class<?>> read(URL url, String packageName, ClassLoader classLoader) {
        try {
            URLConnection connection = url.openConnection();
            if(connection instanceof JarURLConnection) {
                JarFile jarFile = ((JarURLConnection)connection).getJarFile();
                Enumeration<JarEntry> jarEntries = jarFile.entries();

                List<Class<?>> classList = new ArrayList<>();
                while(jarEntries.hasMoreElements()) {
                    JarEntry entry = jarEntries.nextElement();
                    if(entry == null) continue;
                    Class<?> cls = getClassFromEntry(entry, packageName, classLoader);
                    if(cls == null) continue;
                    classList.add(cls);
                }
                return classList;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private Class<?> getClassFromEntry(JarEntry jarEntry, String packageName, ClassLoader classLoader) {
        String name = jarEntry.getName();
        if(!name.endsWith(CLASS_FILE_SUFFIX)) return null;
        if(!name.contains(packageName)) return null;

        String className = name.substring(0, name.length() - CLASS_FILE_SUFFIX.length()).replace("/",".");
        return ClassUtils.forName(className, classLoader);
    }
}
