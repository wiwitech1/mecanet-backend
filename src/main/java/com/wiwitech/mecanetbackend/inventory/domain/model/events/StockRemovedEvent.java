package com.wiwitech.mecanetbackend.inventory.domain.model.events;

/**
 * Evento emitido cuando se remueve stock de un item
 */
public record StockRemovedEvent(
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
    String reference,       // Referencia externa
    Long machineId         // ID de la máquina donde se usó
) {} 