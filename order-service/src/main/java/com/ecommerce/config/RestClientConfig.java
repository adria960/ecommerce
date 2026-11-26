package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient productRestClient(
            RestClient.Builder builder,
            @Value("${product.service.url}") String url) {

        return builder
                .baseUrl(url)
                .requestFactory(requestFactory())
                .build();
    }

    @Bean
    public RestClient inventoryRestClient(
            RestClient.Builder builder,
            @Value("${inventory.service.url}") String url) {

        return builder
                .baseUrl(url)
                .requestFactory(requestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory requestFactory() {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(2000); // Connection timeout - 2 seconds Establishing a connection
        factory.setReadTimeout(3000); // Read timeout 3 seconds - Waiting for response data

        return factory;
    }
}
