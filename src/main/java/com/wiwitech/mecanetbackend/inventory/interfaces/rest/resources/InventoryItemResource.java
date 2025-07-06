package com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record InventoryItemResource(
    Long id,
    String sku,
    String name,
    String description,
    String category,
    String unit,
    BigDecimal unitPrice,
    Integer currentStock,
    Integer minimumStock,
    String location,
    Set<Long> compatibleMachineIds,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 