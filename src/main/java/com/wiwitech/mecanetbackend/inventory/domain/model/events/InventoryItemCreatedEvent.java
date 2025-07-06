package com.wiwitech.mecanetbackend.inventory.domain.model.events;

/**
 * Evento emitido cuando se crea un nuevo item de inventario
 */
public record InventoryItemCreatedEvent(
    Long itemId,
    String sku,
    String name,
    String category,
    Long plantId,
    Long tenantId
) {} 