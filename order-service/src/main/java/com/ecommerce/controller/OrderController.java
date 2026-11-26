package com.ecommerce.controller;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestBody OrderRequest request) {

        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @PathVariable Long id) {

        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders(
            @RequestParam(required = false) String customerId) {

        if (customerId != null) {
            return orderService.getOrdersByCustomerId(customerId);
        }

        return orderService.getAllOrders();
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long id) {

        return orderService.cancelOrder(id);
    }
}