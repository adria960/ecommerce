package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.exception.DuplicateSkuException;
import com.ecommerce.exception.InvalidPriceException;
import com.ecommerce.exception.InvalidProductNameException;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // CREATE
    public ProductResponse createProduct(ProductRequest request) {

        validateProduct(request);
        validateUniqueSku(request.skuCode());

        Product product = ProductMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    // READ ALL
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    // READ BY ID
    public ProductResponse getProductById(String id) {
        Product product = findProductOrThrow(id);

        return ProductMapper.toResponse(product);
    }

    // UPDATE
    public ProductResponse updateProduct(
            String id, ProductRequest request) {

        Product product = findProductOrThrow(id);
        validateProduct(request);

        if (!Objects.equals(product.getSkuCode(), request.skuCode())) {
            validateUniqueSku(request.skuCode());
        }

        ProductMapper.updateEntity(product, request);
        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toResponse(updatedProduct);
    }

    // DELETE
    public void deleteProduct(String id) {

        Product product = findProductOrThrow(id);

        productRepository.delete(product);
    }

    // PRIVATE HELPER METHODS
    private Product findProductOrThrow(String id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void validateProduct(ProductRequest request) {
        validatePrice(request.price());
        validateName(request.name());
    }

    private void validateUniqueSku(String skuCode) {

        if (productRepository.existsBySkuCode(skuCode)) {
            throw new DuplicateSkuException(skuCode);
        }
    }

    private void validatePrice(BigDecimal price) {

        if (price == null ||
                price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException(price);
        }
    }

    private void validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new InvalidProductNameException();
        }
    }
}
