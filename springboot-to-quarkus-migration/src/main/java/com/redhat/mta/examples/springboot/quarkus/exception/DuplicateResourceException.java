package com.redhat.mta.examples.springboot.quarkus.exception;

/**
 * Duplicate Resource Exception
 * 
 * Migration considerations:
 * - Custom exceptions work the same in Quarkus
 * - Exception handling will need to be converted from @ControllerAdvice to @Provider ExceptionMapper
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
