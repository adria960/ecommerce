package com.ecommerce.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String skuCode) {
        super("Inventory not found for SKU: " + skuCode);
    }
}