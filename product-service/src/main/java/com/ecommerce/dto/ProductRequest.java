package com.ecommerce.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String skuCode,
        String name,
        String description,
        BigDecimal price
){}