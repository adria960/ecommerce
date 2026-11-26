package com.ecommerce.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        String skuCode,
        Integer quantity
) {
}

// The customer specifies what to purchase and in what quantity. Product Service determines the price.