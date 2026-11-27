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

/*
    This event represents a business fact: an order has been successfully created and persisted.

    We include an eventId for future consumer-level idempotency.

    Notice that we do not include the entire Order JPA entity in the Kafka message. The event has its own independent contract.
 */