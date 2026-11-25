package com.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    private String skuCode;
    private String name;
    private String description;
    private BigDecimal price;
}