package com.ecommerce.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderPlacedEvent(
        String eventId,
        Long orderId,
        String orderNumber,
        String customerId,
        BigDecimal totalAmount,
        Instant createdAt
) {
}