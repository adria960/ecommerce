package com.ecommerce.mapper;

import com.ecommerce.dto.InventoryRequest;
import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.model.Inventory;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static Inventory toEntity(InventoryRequest request) {

        Inventory inventory = new Inventory();

        inventory.setSkuCode(request.skuCode());
        inventory.setQuantity(request.quantity());

        return inventory;
    }

    public static InventoryResponse toResponse(
            Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getSkuCode(),
                inventory.getQuantity()
        );
    }
}