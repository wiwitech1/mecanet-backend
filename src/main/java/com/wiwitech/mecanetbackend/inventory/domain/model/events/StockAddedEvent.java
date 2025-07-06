package com.wiwitech.mecanetbackend.inventory.domain.model.events;

import java.math.BigDecimal;

/**
 * Evento emitido cuando se agrega stock a un item
 */
public record StockAddedEvent(
    Long itemId,
    String sku,
    String name,
    Integer quantity,
    Integer previousStock,
    Integer newStock,
    String reason,
    Long userId,
    Long plantId,
    Long tenantId,
    
    // NUEVOS CAMPOS PARA AUDITORÍA
    BigDecimal unitCost,    // Costo unitario de esta operación
    String reference        // Referencia externa
) {} 