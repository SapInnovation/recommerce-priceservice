package com.sapient.service.price.exception;

/**
 * Created by hgupta.
 */
@SuppressWarnings("serial")
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("Product not found with id " + productId);
    }
}
