package com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record StockMovementResource(
    Long id,
    Long itemId,
    String itemSku,
    String itemName,
    String movementType,
    Integer quantity,
    Integer previousStock,
    Integer newStock,
    String reason,
    Long userId,
    String userName,
    LocalDateTime createdAt,
    BigDecimal unitCost,
    BigDecimal totalCost
) {} 