package com.ecommerce.dto;

import java.util.List;

public record OrderRequest(
        String customerId,
        List<OrderItemRequest> items
) {
}