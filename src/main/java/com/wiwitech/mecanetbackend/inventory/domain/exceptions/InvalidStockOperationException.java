package com.wiwitech.mecanetbackend.inventory.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta realizar una operación de stock inválida
 */
public class InvalidStockOperationException extends RuntimeException {
    public InvalidStockOperationException(String message) {
        super(message);
    }
} 