package com.ecommerce.mapper;

import com.ecommerce.dto.*;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static Order toEntity(OrderRequest request) {

        Order order = new Order();
        order.setCustomerId(request.customerId());

        request.items().forEach(item ->
                order.addItem(toItemEntity(item))
        );

        return order;
    }

    private static OrderItem toItemEntity(
            OrderItemRequest request) {

        OrderItem item = new OrderItem();

        item.setSkuCode(request.skuCode());
        item.setQuantity(request.quantity());
        item.setPrice(request.price());

        return item;
    }

    public static OrderResponse toResponse(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems()
                        .stream()
                        .map(OrderMapper::toItemResponse)
                        .toList()
        );
    }

    private static OrderItemResponse toItemResponse(
            OrderItem item) {

        return new OrderItemResponse(
                item.getId(),
                item.getSkuCode(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}