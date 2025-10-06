package com.redhat.mta.examples.springboot.quarkus.exception;

/**
 * Resource Not Found Exception
 * 
 * Migration considerations:
 * - Custom exceptions work the same in Quarkus
 * - Exception handling will need to be converted from @ControllerAdvice to @Provider ExceptionMapper
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


