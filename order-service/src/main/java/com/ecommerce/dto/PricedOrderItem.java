package com.ecommerce.dto;

import java.math.BigDecimal;

public record PricedOrderItem(
        String skuCode,
        Integer quantity,
        BigDecimal price
) {
}

// This is an internal DTO containing the information obtained after communicating with Product Service.

// We keep OrderItemResponse and the OrderItem entity unchanged because
// the purchase price must be stored as part of the order history.