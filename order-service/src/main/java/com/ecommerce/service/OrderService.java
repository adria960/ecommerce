package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.exception.InvalidOrderException;
import com.ecommerce.exception.OrderNotFoundException;
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

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // - DTO mapping takes place inside the transaction, so lazy-loaded order items remain accessible.
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        validateOrder(request);

        Order order = OrderMapper.toEntity(request);

        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setTotalAmount(calculateTotal(request));

        //
        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {

        return OrderMapper.toResponse(findOrderOrThrow(id));
    }

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByCustomerId(
            String customerId) {

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    //  - We do not explicitly call save() in cancelOrder(). Hibernate dirty checking will persist the status change.
    //  - DTO mapping takes place inside the transaction, so lazy-loaded order items remain accessible.
    @Transactional
    public OrderResponse cancelOrder(Long id) {

        // 1. Hibernate loads the entity from MySQL and caches a snapshot in memory
        Order order = findOrderOrThrow(id);

        // 2. You modify the Java object directly
        order.setStatus(OrderStatus.CANCELLED);
        // 3. No repository.save() needed!
        // When this method finishes, @Transactional commits and Hibernate automatically runs:
        // UPDATE orders SET status = ? WHERE id = ?

        return OrderMapper.toResponse(order);
    }

    private Order findOrderOrThrow(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(id));
    }

    private BigDecimal calculateTotal(OrderRequest request) {

        return request.items()
                .stream()
                .map(item -> item.price().multiply(
                        BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

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

            if (item.price() == null ||
                    item.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidOrderException(
                        "Price must be positive");
            }
        }
    }
}
/*
Important implementation details
    - @Transactional(readOnly = true) is the default for service methods.
    - Method-level @Transactional overrides it for create and cancel operations.
    - We do not explicitly call save() in cancelOrder(). Hibernate dirty checking will persist the status change.
    - CascadeType.ALL allows order items to be persisted together with the order.
    - DTO mapping takes place inside the transaction, so lazy-loaded order items remain accessible.
 */

/*
    Dirty checking is a feature in database frameworks (like Hibernate/JPA) that automatically tracks changes
    made to an object in memory and updates the database for you—without you having to manually call a save() or update() method.
 */

/*
for a service using MySQL (JPA/Hibernate) and @Transactional, dirty checking works automatically.
When you wrap a method in @Transactional, Spring and Hibernate manage the database session from the moment
 the method starts until it finishes.
 */

/*
3 Rules to keep in mind so it doesn't trip you up - dirty checking

• It must be inside @Transactional:
  Without @Transactional on the method (or class), Hibernate closes its tracking session right after findById(),
  so changes made afterward won't be detected or saved.

• The object must come from the database first:
  Dirty checking only watches "managed" entities loaded via findById() (or similar queries).   ☺
  If you create a new Product() yourself and set its ID, Hibernate won't auto-save it without save().

• A save() call is harmless (just redundant):
  If a developer still writes productRepository.save(product) at the end of a @Transactional method,
  it won't break anything—Hibernate simply sees the entity is already tracked and performs the same single UPDATE.
 */