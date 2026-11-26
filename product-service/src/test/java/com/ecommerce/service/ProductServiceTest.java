package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;

import com.ecommerce.exception.DuplicateSkuException;
import com.ecommerce.exception.InvalidPriceException;
import com.ecommerce.exception.InvalidProductNameException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest createValidProductRequest() {
        return new ProductRequest(
                "LAPTOP-001",
                "Laptop",
                "Laptop description",
                new BigDecimal("999.99")
        );
    }

    @Test
    void shouldCreateProduct() {

        // Arrange
        ProductRequest request = createValidProductRequest();

        Product savedProduct = ProductMapper.toEntity(request);
        savedProduct.setId("123");

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        ProductResponse result = productService.createProduct(request);

        // Assert
        assertEquals("123", result.id());
        assertEquals(request.skuCode(), result.skuCode());
        assertEquals(request.name(), result.name());
        assertEquals(request.description(), result.description());
        assertEquals(request.price(), result.price());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldRejectDuplicateSku() {

        ProductRequest request = createValidProductRequest();

        when(productRepository.existsBySkuCode(request.skuCode()))
                .thenReturn(true);

        assertThrows(
                DuplicateSkuException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-10.00"})
    void shouldRejectInvalidPrice(String price) {

        ProductRequest request = new ProductRequest(
                "LAPTOP-001",
                "Laptop",
                "Laptop description",
                price == null ? null : new BigDecimal(price)
        );

        assertThrows(
                InvalidPriceException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectEmptyProductName(String name) {

        ProductRequest request = new ProductRequest(
                "LAPTOP-001",
                name,
                "Laptop description",
                new BigDecimal("999.99")
        );

        assertThrows(
                InvalidProductNameException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

}
