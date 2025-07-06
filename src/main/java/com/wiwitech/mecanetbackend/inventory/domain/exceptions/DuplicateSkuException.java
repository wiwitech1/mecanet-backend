package com.wiwitech.mecanetbackend.inventory.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un item con SKU duplicado
 */
public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String message) {
        super(message);
    }
} 