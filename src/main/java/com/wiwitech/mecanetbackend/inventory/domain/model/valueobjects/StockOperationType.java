package com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects;

/**
 * Tipos de operaciones de stock
 */
public enum StockOperationType {
    IN,                 // Entrada de stock
    OUT,                // Salida de stock
    ADJUSTMENT,         // Ajuste de inventario
    TRANSFER,           // Transferencia entre ubicaciones
    RESERVATION,        // Reserva de stock
    RELEASE             // Liberación de reserva
} 