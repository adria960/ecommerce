package com.ecommerce.service;

import com.ecommerce.exception.DuplicateSkuException;
import com.ecommerce.exception.InvalidPriceException;
import com.ecommerce.exception.InvalidProductNameException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    //	1. Create product
    @Test
    void shouldCreateProduct() {

        // Arrange
        Product product = new Product();
        product.setName("Laptop");

        Product savedProduct = new Product();
        savedProduct.setId("123");
        savedProduct.setName("Laptop");
        //added in 3rd iteration
        product.setPrice(new BigDecimal("999.99"));

        when(productRepository.save(product))
                .thenReturn(savedProduct);

        // Act
        Product result = productService.createProduct(product);

        // Assert
        assertSame(savedProduct, result);
        verify(productRepository).save(product);
    }

    // 2.
    @Test
    void shouldRejectDuplicateSku() {

        // Arrange
        //You create a pretend product—a "Laptop" with the ID code "LAPTOP-001".
//        Product product = new Product();
//        product.setSkuCode("LAPTOP-001");
//        product.setName("Laptop");
//        product.setPrice(new BigDecimal("999.99"));
//
        // In REFACTTOR of 3rd iteration
        Product product = createValidProduct();

        when(productRepository.existsBySkuCode("LAPTOP-001"))
                .thenReturn(true);

        //It tells the fake productRepository to return true ...
        //Your database is replaced by a fake assistant (the Mock).
        // You whisper to the assistant: "If anyone asks whether 'LAPTOP-001' is already in the catalog, say Yes."
        when(productRepository.existsBySkuCode("LAPTOP-001"))
                .thenReturn(true);

        // Act & Assert
        //You hand the new Laptop to your real Product Service and say, "Save this."
        //• The service checks with the fake assistant: "Do we already have 'LAPTOP-001'?"
        //• The assistant follows your script and says: "Yes."
        //• Your test checks that the service immediately stops and sounds an alarm (throws an error) instead of accepting the duplicate.
        assertThrows(
                DuplicateSkuException.class,
                () -> productService.createProduct(product)
        );

        //Finally, you look at the fake assistant and confirm:
        // "Make sure you never actually put that laptop into the filing cabinet."
        //If the alarm went off AND nothing was saved to the cabinet, the test passes!
        verify(productRepository, never()).save(any(Product.class));
    }

    // 3
    @ParameterizedTest
    @NullSource // it's red but no compile error
    @ValueSource(strings = {"0", "-10.00"})
    void shouldRejectInvalidPrice(String price) {

        // Arrange
//        Product product = new Product();
//        product.setSkuCode("LAPTOP-001");
//        product.setName("Laptop");
        //REFACTOR in 3rd iteration
        Product product = createValidProduct();

        product.setPrice(
                price == null ? null : new BigDecimal(price)
        );

        // Act & Assert
        assertThrows(
                InvalidPriceException.class,
                () -> productService.createProduct(product)
        );

        verify(productRepository, never()).save(any(Product.class));
    }


    private Product createValidProduct() {
        Product product = new Product();
        product.setSkuCode("LAPTOP-001");
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        return product;
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectEmptyProductName(String name) {

        // Arrange
        Product product = createValidProduct();
        product.setName(name);

        // Act & Assert
        assertThrows(
                InvalidProductNameException.class,
                () -> productService.createProduct(product)
        );

        verify(productRepository, never()).save(any(Product.class));
    }
}
/*
1 2 3
Stub the repository (when...thenReturn):
It tells the fake productRepository to return true whenever existsBySkuCode("LAPTOP-001") is called.
This simulates a database that already contains a product with that SKU.

Expected: RED
Expected DuplicateSkuException to be thrown, but nothing was thrown.
The test fails because our current implementation saves the product without checking its SKU.

4. GREEN – Minimum implementation
Location: src/main/java/com/ecommerce/service/ProductService.java

Modify the existing createProduct() method:

4th iteration
Test execution	Input	Provided by
[1] name=null	null	@NullAndEmptySource
[2] name=	"" (empty string)	@NullAndEmptySource
[3] name=	"   " (three spaces)	@ValueSource
 */