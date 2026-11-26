package com.ecommerce.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(
            @Qualifier("inventoryRestClient")
            RestClient restClient) {

        this.restClient = restClient;
    }

    public boolean isInStock(String skuCode, Integer quantity) {

        Boolean available = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/inventory/availability")
                        .queryParam("skuCode", skuCode)
                        .queryParam("quantity", quantity)
                        .build())
                .retrieve()
                .body(Boolean.class);

        return Boolean.TRUE.equals(available);
    }
}
/*
    The uriBuilder constructs the following URL, for example:
    GET http://localhost:8083/api/inventory/availability?skuCode=LAPTOP-001&quantity=2

    6. How synchronous communication works
    RestClient is synchronous and blocking. The calling thread waits for the remote service to respond.

    If the remote service fails or times out, RestClient throws an exception.
    Resilience4J will help us handle such failures in subsequent steps.
 */