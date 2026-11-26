package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * This service is responsible for persisting the order within a local database transaction.
 */
@Service
public class OrderPersistenceService {

    private final OrderRepository orderRepository;

    public OrderPersistenceService(
            OrderRepository orderRepository) {

        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse saveOrder(
            OrderRequest request,
            List<PricedOrderItem> pricedItems) {

        Order order = OrderMapper.toEntity(
                request, pricedItems);

        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setTotalAmount(calculateTotal(pricedItems));

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toResponse(savedOrder);
    }

    private BigDecimal calculateTotal(
            List<PricedOrderItem> items) {

        return items.stream()
                .map(item -> item.price().multiply(
                        BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

/*
    6. Transaction boundaries – Architectural decision
    Previously, we had:
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Validation
        // Order creation
        // Database persistence
    }

    If we simply add REST calls inside this method, the transaction would also remain open while waiting for remote services.

    Approach 1 — Long-running transaction - Txn remains open during network calls.
    All in Txn ----> Validate -> Product REST -> Inventory REST -> Save

    Approach 2 — Short database transaction -
   ONLY Save is in Txn -----> Validate -> Product REST -> Inventory REST -> Save

    We will implement Approach 2 by separating order orchestration from transactional persistence.
 */