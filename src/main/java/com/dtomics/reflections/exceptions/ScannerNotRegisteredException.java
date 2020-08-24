package com.dtomics.reflections.exceptions;

import com.dtomics.reflections.scanners.Scanner;

public class ScannerNotRegisteredException extends RuntimeException {

    private Class<? extends Scanner> scannerClass;
    public ScannerNotRegisteredException(Class<? extends Scanner> scannerClass) {
        super(String.format("%s is not registered",scannerClass.getCanonicalName()));
        this.scannerClass = scannerClass;
    }

    public Class<? extends Scanner> getScannerClass() {
        return scannerClass;
    }
}
