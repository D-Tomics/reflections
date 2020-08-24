package com.dtomics.reflections.scanners;

import com.dtomics.reflections.Cache;

public interface Scanner {
    void onScan(final Class<?> type, Cache cache);
}
