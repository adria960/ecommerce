package com.ecommerce.dto;

public record InventoryRequest(
        String skuCode,
        Integer quantity
) {
}
