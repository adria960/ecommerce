package com.ecommerce.exception;

import java.math.BigDecimal;

public class InvalidPriceException extends RuntimeException {

    public InvalidPriceException(BigDecimal price) {
        super("Invalid product price: " + price);
    }
}
