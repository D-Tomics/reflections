package com.dtomics.reflections;

import com.dtomics.reflections.classReaders.ClassReader;
import com.dtomics.reflections.exceptions.ScannerAlreadyRegisteredException;
import com.dtomics.reflections.scanners.AnnotatedClassFieldScanner;
import com.dtomics.reflections.scanners.AnnotatedClassScanner;
import com.dtomics.reflections.scanners.AnnotatedExecutableScanner;
import com.dtomics.reflections.scanners.AnnotatedFieldScanner;
import com.dtomics.reflections.scanners.FieldScanner;
import com.dtomics.reflections.scanners.Scanner;
import com.dtomics.reflections.utils.ClassUtils;
import com.dtomics.reflections.utils.ReflectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.dtomics.reflections.ReflectionConstants.CLASS_FILE_SUFFIX;
import static com.dtomics.reflections.utils.ClassUtils.forName;
import static com.dtomics.reflections.utils.ClassUtils.index;

public final class Reflections {

    private HashSet<Scanner> scanners;
    private HashSet<ClassReader> classReaders;
    private String packageName;
    private ClassLoader classLoader;
    private Cache cache;
    private boolean loadWithProvidedClassLoader;

    private ExecutorService executorService;

    public Reflections(Class<?> cls, ClassLoader classLoader) {
        this(cls.getCanonicalName() + CLASS_FILE_SUFFIX, classLoader);
    }

    public Reflections(String packageName, ClassLoader classLoader) {
        this(packageName, classLoader, false, Executors.newCachedThreadPool());
    }

    public Reflections(String packageName, ClassLoader classLoader, boolean loadWithProvidedClassLoader, ExecutorService executorService) {
        if (packageName == null)
            throw new IllegalArgumentException("package cannot be null");

        this.cache = new Cache();
        this.packageName = packageName;
        this.scanners = new HashSet<>();
        this.classReaders = new HashSet<>();

        this.classLoader = classLoader;
        this.loadWithProvidedClassLoader = loadWithProvidedClassLoader;

        this.executorService = executorService;
    }


    public void scan() {
        try {
            if (classReaders.isEmpty()) return;
            List<Future<?>> futures = new ArrayList<>();
            String resource =
                    packageName.endsWith(CLASS_FILE_SUFFIX) ?
                            packageName
                                    .substring(0, packageName.length() - CLASS_FILE_SUFFIX.length())
                                    .replace(".", "/")
                                    .concat(CLASS_FILE_SUFFIX) :
                            packageName.replace(".", "/");

            Enumeration<URL> resources = classLoader.getResources(resource);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                if (url == null) continue;
                if (executorService != null) {
                    futures.add(executorService.submit(() -> readClass(url, packageName)));
                    continue;
                }
                readClass(url, packageName);
            }

            if (executorService != null) {
                for (Future future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                executorService.shutdown();
            }
        } catch (IOException e) {
            throw new IllegalStateException("error scanning classes :: " + e.getLocalizedMessage());
        }
    }

    public void addScanner(Scanner scanner) {
        this.cache.register(scanner.getClass());
        if (!this.scanners.add(scanner))
            throw new ScannerAlreadyRegisteredException(scanner.getClass());
    }

    public void addReader(ClassReader reader) {
        if (!this.classReaders.add(reader))
            throw new IllegalArgumentException("trying to add " + reader.getClass().getName() + " reader that already exists");
    }

    public <T extends Scanner> T getScanner(Class<T> scannerType) {
        for (Scanner scanner : scanners)
            if (scanner.getClass().equals(scannerType))
                return scannerType.cast(scanner);
        return null;
    }

    public List<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotationType) {
        return forName(cache.getElementsOf(AnnotatedClassScanner.class).get(index(annotationType)), loader());
    }

    public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotationType, Class<?> of) {
        Set<String> methodSignatures = cache.getElementsOf(AnnotatedExecutableScanner.class).get(index(annotationType));
        return methodSignatures.stream()
                .filter(ReflectionUtils::isMethod)
                .map(signature -> ClassUtils.getDeclaredMethod(
                        of,
                        ReflectionUtils.extractMethodName(signature),
                        ReflectionUtils.getParameterTypesFromSignature(signature, of, loader()).toArray(new Class<?>[0])
                        )
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Constructor> getConstructorsAnnotatedWith(Class<? extends Annotation> annotationType, Class<?> of) {
        Set<String> constructorSignatures = cache.getElementsOf(AnnotatedExecutableScanner.class).get(index(annotationType));
        return constructorSignatures.stream()
                .filter(ReflectionUtils::isConstructor)
                .map(signature -> ClassUtils.getDeclaredConstructor(
                        of,
                        ReflectionUtils.getParameterTypesFromSignature(signature, of, loader()).toArray(new Class<?>[0])
                        )
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Field> getFieldsOf(Class<?> of) {
        Set<String> filedSignatures = cache.getElementsOf(FieldScanner.class).get(index(of));
        return filedSignatures != null ? filedSignatures.stream()
                .map(signature -> ClassUtils.getDeclaredField(of, ReflectionUtils.extractFieldName(signature)))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : null;
    }

    public Set<Field> getFieldsOfAnnotatedClass(Class<?> of) {
        Set<String> fieldSignatures = cache.getElementsOf(AnnotatedClassFieldScanner.class).get(index(of));
        return fieldSignatures.stream()
                .map(signature -> ClassUtils.getDeclaredField(of, ReflectionUtils.extractFieldName(signature)))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> annotationType, Class<?> of) {
        Set<String> fieldSignatures = cache.getElementsOf(AnnotatedFieldScanner.class).get(index(annotationType));
        return fieldSignatures.stream()
                .map(signature -> ClassUtils.getDeclaredField(of, ReflectionUtils.extractFieldName(signature)))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void clear() {
        cache.clear();
        scanners.clear();
    }

    private void onScan(final Class<?> type) {
        if (type == null) return;
        for (Scanner scanner : scanners) {
            scanner.onScan(type, cache);
        }
    }

    private void readClass(URL url, String packageName) {
        for (ClassReader classReader : classReaders)
            classReader.read(url, packageName, loader()).forEach(this::onScan);
    }

    private ClassLoader loader() {
        return loadWithProvidedClassLoader ? classLoader : null;
    }

}
