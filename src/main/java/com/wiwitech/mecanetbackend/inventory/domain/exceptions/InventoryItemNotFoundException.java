package com.wiwitech.mecanetbackend.inventory.domain.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un item de inventario
 */
public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException(String message) {
        super(message);
    }
} 