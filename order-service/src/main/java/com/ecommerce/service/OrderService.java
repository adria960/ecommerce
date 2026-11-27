package com.ecommerce.service;

import com.ecommerce.client.InventoryClient;
import com.ecommerce.client.ProductClient;
import com.ecommerce.dto.*;
import com.ecommerce.event.OrderPlacedEvent;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.InvalidOrderException;
import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.producer.OrderEventProducer;
import com.ecommerce.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * OrderService now acts as an orchestrator for order creation.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderPersistenceService orderPersistenceService;

    private final OrderEventProducer orderEventProducer;

    public OrderService(
            OrderRepository orderRepository,
            ProductClient productClient,
            InventoryClient inventoryClient,
            OrderPersistenceService orderPersistenceService,
            OrderEventProducer orderEventProducer) {

        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
        this.orderPersistenceService = orderPersistenceService;
        this.orderEventProducer = orderEventProducer;
    }

    /**
     *
     * @param request
     * @return OrderResponse
     */
    public OrderResponse createOrder(OrderRequest request) {

        validateOrder(request);

        List<PricedOrderItem> pricedItems = new ArrayList<>();

        for (OrderItemRequest item : request.items()) {

            ProductResponse product =
                    productClient.getProduct(item.skuCode());

            boolean available = inventoryClient.isInStock(
                    item.skuCode(), item.quantity());

            if (!available) {
                throw new InsufficientStockException(
                        item.skuCode());
            }

            pricedItems.add(new PricedOrderItem(
                    item.skuCode(),
                    item.quantity(),
                    product.price()
            ));
        }

        // *** Save Order in LOCAL DATABASE TRANSACTION
        OrderResponse response =
                orderPersistenceService.saveOrder(
                        request, pricedItems);

        OrderPlacedEvent event = new OrderPlacedEvent(
                UUID.randomUUID().toString(),
                response.id(),
                response.orderNumber(),
                response.customerId(),
                response.totalAmount(),
                response.createdAt()
        );

        // Publish Order for Notif service
        orderEventProducer.publishOrderPlaced(event);

        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        return OrderMapper.toResponse(
                findOrderOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(
            String customerId) {

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {

        Order order = findOrderOrThrow(id);
        order.setStatus(OrderStatus.CANCELLED);

        return OrderMapper.toResponse(order);
    }

    /**
     *
     * @param id
     * @return Order
     */
    private Order findOrderOrThrow(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(id));
    }

    /**
     *
     * @param request
     */
    private void validateOrder(OrderRequest request) {

        if (request.customerId() == null ||
                request.customerId().isBlank()) {
            throw new InvalidOrderException(
                    "Customer ID is required");
        }

        if (request.items() == null ||
                request.items().isEmpty()) {
            throw new InvalidOrderException(
                    "Order must contain at least one item");
        }

        Set<String> skuCodes = new HashSet<>();

        for (OrderItemRequest item : request.items()) {

            if (item == null) {
                throw new InvalidOrderException(
                        "Invalid order item");
            }

            if (item.skuCode() == null ||
                    item.skuCode().isBlank()) {
                throw new InvalidOrderException(
                        "SKU code is required");
            }

            if (item.quantity() == null ||
                    item.quantity() <= 0) {
                throw new InvalidOrderException(
                        "Quantity must be positive");
            }

            if (!skuCodes.add(item.skuCode())) {
                throw new InvalidOrderException(
                        "Duplicate SKU in order: "
                                + item.skuCode());
            }
        }
    }
}

/*
    I have introduced one additional business rule: duplicate SKUs within the same order are rejected.
    This prevents multiple order lines from independently passing availability checks against the same stock quantity.
    An alternative would be to aggregate their quantities.

    9. Why a separate OrderPersistenceService?
    This is an important Spring interview question.

    Why not simply create a transactional method inside OrderService?
    public OrderResponse createOrder(OrderRequest request) {

        // Remote REST calls
        return saveOrder(request);
    }

    @Transactional
    public OrderResponse saveOrder(OrderRequest request) {
        // Database persistence
    }

    This would encounter the Spring self-invocation problem. Read in doc.
    Spring's default proxy-based transaction management intercepts calls coming through the proxy.
    A direct call between methods of the same object bypasses it.

    By using a separate Spring-managed bean, our call passes through the transaction proxy.
 */

/*
    Because saveOrder() is executed through a separate transactional Spring bean,
    its transaction commits before control returns to createOrder(),
    in our current non-transactional orchestration flow.

   *** However, there is still a problem:
        What happens if the database commit succeeds but Kafka publishing fails?
        The order exists in MySQL, but Notification Service may never receive the event.

   *** This is known as the dual-write problem.

    We deliberately leave this limitation for now because we will solve it using the
    Transactional Outbox pattern later.
 */
