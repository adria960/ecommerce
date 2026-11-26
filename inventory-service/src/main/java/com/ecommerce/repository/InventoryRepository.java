package com.ecommerce.repository;

import com.ecommerce.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findBySkuCode(String skuCode);

    boolean existsBySkuCode(String skuCode);

    // This method is particularly important because Order Service will use our stock availability endpoint.
    // Spring Data JPA derives a query equivalent to checking:
    // WHERE sku_code = ? AND quantity >= ?
    boolean existsBySkuCodeAndQuantityGreaterThanEqual(
            String skuCode, Integer quantity);
}