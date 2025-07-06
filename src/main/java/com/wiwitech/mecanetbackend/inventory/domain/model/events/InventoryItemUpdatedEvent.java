package com.wiwitech.mecanetbackend.inventory.domain.model.events;

/**
 * Evento emitido cuando se actualiza un item de inventario
 */
public record InventoryItemUpdatedEvent(
    Long itemId,
    String sku,
    String name,
    Long plantId,
    Long tenantId
) {} 