package com.ecommerce.dto;

import java.math.BigDecimal;

// Important architectural decision: We do not import Java classes from Product Service.
// Each microservice remains independent.
// The REST API contract connects them, not a Java dependency between their projects.
public record ProductResponse(
        String id,
        String skuCode,
        String name,
        String description,
        BigDecimal price
) {
}

