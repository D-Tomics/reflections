package com.dtomics.reflections.exceptions;

public class InvalidSignatureException extends RuntimeException{

    private String signature;
    public InvalidSignatureException(String signature, String signatureFor) {
        super(String.format("%s is an invalid %s signature", signature, signatureFor));
    }

    public String getSignature() {
        return signature;
    }
}
