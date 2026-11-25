package com.ecommerce.exception;

public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String skuCode) {
        super("Product with SKU " + skuCode + " already exists");
    }
}