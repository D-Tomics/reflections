package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;

import static com.dtomics.reflections.utils.ClassUtils.index;

public class SubClassScanner implements Scanner {

    private boolean excludeObject;

    public SubClassScanner() {
        this(true);
    }

    public SubClassScanner(boolean excludeObject) {
        this.excludeObject = excludeObject;
    }

    @Override
    public void onScan(Class<?> type, Cache cache) {
        Class<?> superClass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();

        if(superClass != null && (!excludeObject || !superClass.equals(Object.class)))
            cache.put(SubClassScanner.class, index(superClass), index(type));

        if(interfaces != null) {
            for(Class<?> interfaceType : interfaces)
                cache.put(SubClassScanner.class, index(interfaceType), index(type));
        }
    }
}
