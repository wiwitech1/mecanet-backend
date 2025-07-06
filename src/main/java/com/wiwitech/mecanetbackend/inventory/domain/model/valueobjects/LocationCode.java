package com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object para códigos de ubicación
 */
@Embeddable
public record LocationCode(String value) {
    public LocationCode {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Location code cannot be null or empty");
        }
        if (value.length() > 20) {
            throw new IllegalArgumentException("Location code cannot exceed 20 characters");
        }
    }
} 