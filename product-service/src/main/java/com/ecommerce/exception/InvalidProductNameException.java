package com.ecommerce.exception;


public class InvalidProductNameException extends RuntimeException {

    public InvalidProductNameException() {
        super("Product name must not be blank");
    }
}