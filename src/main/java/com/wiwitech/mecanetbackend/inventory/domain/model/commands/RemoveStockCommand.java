package com.wiwitech.mecanetbackend.inventory.domain.model.commands;

/**
 * Command para remover stock de un item de inventario
 */
public record RemoveStockCommand(
    Long itemId,
    Integer quantity,
    String reason,
    Long userId,
    
    // NUEVOS CAMPOS PARA AUDITORÍA
    String reference,       // Referencia externa (work order, ticket, etc.)
    Long machineId         // ID de la máquina donde se usó
) {} 