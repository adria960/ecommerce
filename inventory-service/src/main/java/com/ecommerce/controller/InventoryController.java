package com.ecommerce.controller;

import com.ecommerce.dto.InventoryRequest;
import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.service.InventoryService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(
            InventoryService inventoryService) {

        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(
            @RequestBody InventoryRequest request) {

        return inventoryService.createInventory(request);
    }

    @GetMapping
    public List<InventoryResponse> getAllInventory() {

        return inventoryService.getAllInventory();
    }

    @GetMapping("/{skuCode}")
    public InventoryResponse getInventory(
            @PathVariable String skuCode) {

        return inventoryService.getInventory(skuCode);
    }

    @PatchMapping("/{skuCode}/quantity")
    public InventoryResponse updateQuantity(
            @PathVariable String skuCode,
            @RequestParam Integer quantity) {

        return inventoryService.updateQuantity(
                skuCode, quantity);
    }

    @GetMapping("/availability")
    public boolean isInStock(
            @RequestParam String skuCode,
            @RequestParam Integer quantity) {

        return inventoryService.isInStock(
                skuCode, quantity);
    }
}