package com.dtomics.reflections;

import com.dtomics.reflections.exceptions.ScannerNotRegisteredException;
import com.dtomics.reflections.scanners.Scanner;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class Cache {

    private ConcurrentHashMap<Class<? extends Scanner>, Map<String, Set<String>>> cache;
    protected  Cache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public void put(Class<? extends Scanner> scannerClass, String key, String...values) {
        cache
                .computeIfAbsent(scannerClass, sc -> new HashMap<>())
                .computeIfAbsent(key, k -> new HashSet<>())
                .addAll(Arrays.asList(values));
    }

    public Map<String,Set<String>> getElementsOf(Class<? extends Scanner> scannerClass) {
        Map<String, Set<String>> keyValues = cache.get(scannerClass);
        if(keyValues == null)
            throw new ScannerNotRegisteredException(scannerClass);
        return cache.get(scannerClass);
    }

    public Set<String> get(Class<? extends Scanner> scannerClass, String key) {
        return getElementsOf(scannerClass).get(key);
    }

}
