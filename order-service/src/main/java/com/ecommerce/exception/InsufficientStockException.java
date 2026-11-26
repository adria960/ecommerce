package com.ecommerce.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String skuCode) {
        super("Insufficient stock for SKU: " + skuCode);
    }
}