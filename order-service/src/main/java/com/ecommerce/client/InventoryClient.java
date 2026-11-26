package com.ecommerce.client;

import com.ecommerce.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpServerErrorException;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(
            @Qualifier("inventoryRestClient")
            RestClient restClient) {

        this.restClient = restClient;
    }

    @Retry(
            name = "inventoryService",
            fallbackMethod = "inventoryFallback"
    )
    @CircuitBreaker(name = "inventoryService")
    public boolean isInStock(
            String skuCode, Integer quantity) {

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

    private boolean inventoryFallback(
            String skuCode,
            Integer quantity,
            ResourceAccessException ex) {

        throw new ServiceUnavailableException(
                "Inventory Service", ex);
    }

    private boolean inventoryFallback(
            String skuCode,
            Integer quantity,
            HttpServerErrorException ex) {

        throw new ServiceUnavailableException(
                "Inventory Service", ex);
    }

    private boolean inventoryFallback(
            String skuCode,
            Integer quantity,
            CallNotPermittedException ex) {

        throw new ServiceUnavailableException(
                "Inventory Service", ex);
    }
}


/*
    Retry configuration

    Property	Meaning
    ----------------------------------------------------------------
    max-attempts=3	            One initial attempt and two retries
    wait-duration=500ms	        Waiting time between attempts
    ResourceAccessException	    Connection failures and network timeouts
    HttpServerErrorException	Remote HTTP 5xx errors

    We should not retry HTTP 400 or 404 responses because these normally represent client or business errors
    rather than temporary infrastructure failures.

For the same reason, configure our circuit breakers to ignore HTTP 4xx exceptions:
 */
/*
    Fallback method requirements
    A fallback method must have:
    - The same return type as the original method.
    - The same original method parameters.
    - An additional exception parameter.
    Resilience4J selects the most appropriate matching fallback method based on the exception type.
 */

/*
    The uriBuilder constructs the following URL, for example:
    GET http://localhost:8083/api/inventory/availability?skuCode=LAPTOP-001&quantity=2

    6. How synchronous communication works
    RestClient is synchronous and blocking. The calling thread waits for the remote service to respond.

    If the remote service fails or times out, RestClient throws an exception.
    Resilience4J will help us handle such failures in subsequent steps.
 */