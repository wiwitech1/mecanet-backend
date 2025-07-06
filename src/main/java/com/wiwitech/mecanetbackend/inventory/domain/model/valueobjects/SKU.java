package com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object para el código SKU (Stock Keeping Unit)
 */
@Embeddable
public record SKU(String value) {
    public SKU {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("SKU cannot exceed 50 characters");
        }
    }
} 