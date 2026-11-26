package com.ecommerce.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        String skuCode,
        Integer quantity,
        BigDecimal price
) {
}