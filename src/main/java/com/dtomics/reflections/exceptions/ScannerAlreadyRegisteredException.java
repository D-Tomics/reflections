package com.dtomics.reflections.exceptions;


import com.dtomics.reflections.scanners.Scanner;

public class ScannerAlreadyRegisteredException extends RuntimeException {

    private Class<? extends Scanner> scannerClass;

    public ScannerAlreadyRegisteredException(Class<? extends Scanner> scannerClass) {
        super(String.format("%s is already registered ", scannerClass.getCanonicalName()));
        this.scannerClass = scannerClass;
    }

    public Class<? extends Scanner> getScannerClass() {
        return scannerClass;
    }

}
