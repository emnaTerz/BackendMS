package com.emna.micro_service2.exception;

public class ValueExtractionException extends Exception {
    public ValueExtractionException(String message) {
        super(message);
    }

    public ValueExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
