package com.ecommerce.dto;

public record InventoryResponse(
        Long id,
        String skuCode,
        Integer quantity
) {
}