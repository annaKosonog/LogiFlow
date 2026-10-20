package com.io.logiflow.packageConsumer.infrastructure.exception;

public class TruckPackingConflictException extends RuntimeException {

    public TruckPackingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
