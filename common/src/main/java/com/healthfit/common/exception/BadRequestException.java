package com.healthfit.common.exception;

/**
 * Exception thrown for invalid client requests
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
