package com.hashedin.huspark.exception;

public class EncryptionDecryptionException extends RuntimeException {
    public EncryptionDecryptionException(String message,Throwable error) {
        super(message);
    }
}
