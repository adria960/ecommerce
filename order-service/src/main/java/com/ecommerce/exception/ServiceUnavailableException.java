package com.ecommerce.exception;

public class ServiceUnavailableException
        extends RuntimeException {

    public ServiceUnavailableException(
            String serviceName, Throwable cause) {

        super(serviceName + " is temporarily unavailable",
                cause);
    }
}