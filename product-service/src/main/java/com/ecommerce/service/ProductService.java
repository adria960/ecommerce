package com.ecommerce.service;

import com.ecommerce.exception.DuplicateSkuException;
import com.ecommerce.exception.InvalidPriceException;
import com.ecommerce.exception.InvalidProductNameException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //    1. iteration
    //    public Product createProduct(Product product) {
//        return productRepository.save(product);
//    }

    // 2nd iteration
//    public Product createProduct(Product product) {
//
//        if (productRepository.existsBySkuCode(product.getSkuCode())) {
//            throw new DuplicateSkuException(product.getSkuCode());
//        }
//
//        return productRepository.save(product);
//    }
    // 3rd
//    public Product createProduct(Product product) {
//
//        if (product.getPrice() == null ||
//                product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new InvalidPriceException(product.getPrice());
//        }
//
//        if (productRepository.existsBySkuCode(product.getSkuCode())) {
//            throw new DuplicateSkuException(product.getSkuCode());
//        }
//
//        return productRepository.save(product);
//    }

    // 3 Refactor
//    public Product createProduct(Product product) {
//
//        validatePrice(product.getPrice());
//
//        if (productRepository.existsBySkuCode(product.getSkuCode())) {
//            throw new DuplicateSkuException(product.getSkuCode());
//        }
//
//        return productRepository.save(product);
//    }

    private void validatePrice(BigDecimal price) {

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException(price);
        }
    }


    //4
    public Product createProduct(Product product) {

        validatePrice(product.getPrice());

        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidProductNameException();
        }

        if (productRepository.existsBySkuCode(product.getSkuCode())) {
            throw new DuplicateSkuException(product.getSkuCode());
        }

        return productRepository.save(product);
    }
}