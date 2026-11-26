package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    //Spring Data MongoDB will automatically derive the query from the method name.
    boolean existsBySkuCode(String skuCode);

    // For Order service
    Optional<Product> findBySkuCode(String skuCode);
}
