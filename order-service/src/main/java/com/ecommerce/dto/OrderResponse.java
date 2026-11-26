package com.ecommerce.dto;

import com.ecommerce.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}