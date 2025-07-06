package com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources;

import java.math.BigDecimal;

public record InventorySummaryResource(
    Long totalItems,
    Long activeItems,
    Long lowStockItems,
    BigDecimal totalValue,
    Long recentMovements
) {} 