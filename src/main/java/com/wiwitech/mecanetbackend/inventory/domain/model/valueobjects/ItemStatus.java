package com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects;

/**
 * Estados de los items de inventario
 */
public enum ItemStatus {
    ACTIVE,             // Activo y disponible
    INACTIVE,           // Inactivo temporalmente
    DISCONTINUED        // Descontinuado permanentemente
} 