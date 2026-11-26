package com.ecommerce.client;

import com.ecommerce.dto.ProductResponse;
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
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            @Qualifier("productRestClient")
            RestClient restClient) {

        this.restClient = restClient;
    }

    @Retry(
            name = "productService",
            fallbackMethod = "productFallback"
    )
    @CircuitBreaker(name = "productService")
    public ProductResponse getProduct(String skuCode) {

        return restClient.get()
                .uri("/api/products/sku/{skuCode}", skuCode)
                .retrieve()
                .body(ProductResponse.class);
    }

    private ProductResponse productFallback(
            String skuCode,
            ResourceAccessException ex) {

        throw new ServiceUnavailableException(
                "Product Service", ex);
    }

    private ProductResponse productFallback(
            String skuCode,
            HttpServerErrorException ex) {

        throw new ServiceUnavailableException(
                "Product Service", ex);
    }

    private ProductResponse productFallback(
            String skuCode,
            CallNotPermittedException ex) {

        throw new ServiceUnavailableException(
                "Product Service", ex);
    }
}

/*
    Fallback method requirements
    -------------------------------
    A fallback method must have:
    -------------------------------
    - The same return type as the original method.
    - The same original method parameters.
    - An additional exception parameter.

    Resilience4J selects the most appropriate matching fallback method based on the exception type.
 */

/*
    RestClient method flow
    --------------------------------------------
    Method	    Purpose
    --------------------
    get()	    Specifies HTTP GET
    uri()	    Defines the endpoint and parameters
    retrieve()	Executes the HTTP exchange when the response is extracted
    body()	    Deserializes JSON into a Java object
 */