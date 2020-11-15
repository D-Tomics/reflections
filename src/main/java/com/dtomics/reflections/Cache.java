package com.dtomics.reflections;

import com.dtomics.reflections.exceptions.ScannerAlreadyRegisteredException;
import com.dtomics.reflections.exceptions.ScannerNotRegisteredException;
import com.dtomics.reflections.scanners.Scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public final class Cache {

    private ConcurrentHashMap<Class<? extends Scanner>, Map<String, Set<String>>> cache;

    protected Cache() {
        this.cache = new ConcurrentHashMap<>();
    }

    protected void register(Class<? extends Scanner> scanner) {
        if (this.cache.containsKey(scanner))
            throw new ScannerAlreadyRegisteredException(scanner);

        this.cache.put(scanner, new HashMap<>());
    }

    public void put(Class<? extends Scanner> scannerClass, String key, String... values) {
        cache
                .computeIfAbsent(scannerClass, sc -> new HashMap<>())
                .computeIfAbsent(key, k -> new HashSet<>())
                .addAll(Arrays.asList(values));
    }

    public Map<String, Set<String>> getElementsOf(Class<? extends Scanner> scannerClass) {
        Map<String, Set<String>> keyValues = cache.get(scannerClass);
        if (keyValues == null) {
            throw new ScannerNotRegisteredException(scannerClass);
        }
        return keyValues;
    }

    public Set<String> get(Class<? extends Scanner> scannerClass, String key) {
        return getElementsOf(scannerClass).get(key);
    }

    public void clear() {
        if (cache != null)
            cache.clear();
    }

}
