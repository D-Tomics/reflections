package com.dtomics.reflections.classReaders;

import com.dtomics.reflections.utils.ClassUtils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.dtomics.reflections.ReflectionConstants.CLASS_FILE_SUFFIX;

public class ClassFileReader implements ClassReader {

    @Override
    public List<Class<?>> read(URL url, String packageName, ClassLoader classLoader) {
        File dir = new File(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8));
        List<Class<?>> dest = new ArrayList<>();
        if (dir.exists()) {
            if (dir.isDirectory()) {
                return scanDir(dir, packageName, dest, classLoader);
            } else {
                if (dir.getName().endsWith(CLASS_FILE_SUFFIX)) {
                    String fileName = dir.getName();
                    dest.add(getClassFromFile(packageName.isEmpty() ? "%s%s" : "%s.%s", packageName.substring(0, packageName.length() - fileName.length() - 1), dir, classLoader));
                }
            }
        }
        return dest;
    }

    private List<Class<?>> scanDir(File dir, String packageName, List<Class<?>> dest, ClassLoader classLoader) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) return null;

            String format = packageName.isEmpty() ? "%s%s" : "%s.%s";
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    scanDir(file, String.format(format, packageName, fileName), dest, classLoader);
                }
                if (fileName.endsWith(CLASS_FILE_SUFFIX)) {
                    dest.add(getClassFromFile(format, packageName, file, classLoader));
                }
            }
        }
        return dest;
    }

    private Class<?> getClassFromFile(String format, String packageName, File file, ClassLoader classLoader) {
        String fileName = file.getName();
        return ClassUtils.forName(
                String.format(format, packageName, fileName.substring(0, fileName.length() - CLASS_FILE_SUFFIX.length())),
                classLoader
        );
    }
}
