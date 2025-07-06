package com.wiwitech.mecanetbackend.inventory.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command para agregar stock a un item de inventario
 */
public record AddStockCommand(
    Long itemId,
    Integer quantity,
    String reason,
    Long userId,
    
    // NUEVOS CAMPOS PARA AUDITORÍA DE COSTOS
    BigDecimal unitCost,    // Costo unitario de esta compra
    String reference        // Referencia externa (factura, PO, etc.)
) {} 