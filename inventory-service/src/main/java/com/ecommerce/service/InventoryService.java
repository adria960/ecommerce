package com.ecommerce.service;

import com.ecommerce.dto.InventoryRequest;
import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.exception.InventoryNotFoundException;
import com.ecommerce.mapper.InventoryMapper;
import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(
            InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponse createInventory(
            InventoryRequest request) {

        // 1 Quantity Validation
        validateQuantity(request.quantity());

        // 1 Sku Code Validation
        if (request.skuCode() == null ||
                request.skuCode().isBlank()) {
            throw new IllegalArgumentException(
                    "SKU code is required");
        }

        // 2 Sku Code Validation
        if (inventoryRepository.existsBySkuCode(
                request.skuCode())) {

            throw new IllegalArgumentException(
                    "Inventory already exists for SKU: "
                            + request.skuCode());
        }

        // Map request to Inventory entity
        Inventory inventory =
                InventoryMapper.toEntity(request);

        // Save entity tto dba and return mapped response
        return InventoryMapper.toResponse(
                inventoryRepository.save(inventory));
    }

    /**
     *
     * @param skuCode
     * @return InventoryResponse
     */
    public InventoryResponse getInventory(String skuCode) {

        return InventoryMapper.toResponse(
                findInventoryOrThrow(skuCode));
    }

    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(InventoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public InventoryResponse updateQuantity(
            String skuCode, Integer quantity) {

        // Quanttity validation
        validateQuantity(quantity);

        // get inventory by sku
        Inventory inventory = findInventoryOrThrow(skuCode);

        // Update persistent entity
        // Notice that updateQuantity() uses Hibernate dirty checking, just like cancelOrder() in Order Service.
        inventory.setQuantity(quantity);

        return InventoryMapper.toResponse(inventory);
    }

    /**
     *
     * @param skuCode
     * @param quantity
     * @return boolean
     */
    public boolean isInStock(
            String skuCode, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Requested quantity must be positive");
        }

        return inventoryRepository
                .existsBySkuCodeAndQuantityGreaterThanEqual(
                        skuCode, quantity);
    }

    /**
     *
     * @param skuCode
     * @return Inventory
     */
    private Inventory findInventoryOrThrow(String skuCode) {

        return inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() ->
                        new InventoryNotFoundException(skuCode));
    }

    /**
     *
     * @param quantity
     */
    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative or null");
        }
    }
}
// IllegalArgumentException extends RuntimeException