package com.wiwitech.mecanetbackend.inventory.domain.exceptions;

/**
 * Excepción lanzada cuando no hay suficiente stock para una operación
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
} 