package com.ecommerce.client;

import com.ecommerce.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            @Qualifier("productRestClient")
            RestClient restClient) {

        this.restClient = restClient;
    }

    public ProductResponse getProduct(String skuCode) {

        return restClient.get()
                .uri("/api/products/sku/{skuCode}", skuCode)
                .retrieve()
                .body(ProductResponse.class);
    }
}

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