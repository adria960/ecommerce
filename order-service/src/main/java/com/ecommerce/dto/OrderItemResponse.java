package com.ecommerce.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String skuCode,
        Integer quantity,
        BigDecimal price
) {
}