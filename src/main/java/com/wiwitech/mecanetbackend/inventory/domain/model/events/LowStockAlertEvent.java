package com.wiwitech.mecanetbackend.inventory.domain.model.events;

/**
 * Evento emitido cuando el stock de un item está bajo el mínimo
 */
public record LowStockAlertEvent(
    Long itemId,
    String sku,
    String name,
    Integer currentStock,
    Integer minimumStock,
    Long plantId,
    Long tenantId
) {} 