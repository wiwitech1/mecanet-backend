package com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object para identificar items de inventario
 */
@Embeddable
public record InventoryItemId(Long value) {
    public InventoryItemId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Inventory item ID must be positive");
        }
    }
} 