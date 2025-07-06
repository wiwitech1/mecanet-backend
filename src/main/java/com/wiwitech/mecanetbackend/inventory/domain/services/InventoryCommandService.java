package com.wiwitech.mecanetbackend.inventory.domain.services;

import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.commands.*;

/**
 * Servicio de dominio para comandos de inventario
 */
public interface InventoryCommandService {
    
    /**
     * Crear un nuevo item de inventario
     */
    InventoryItem handle(CreateInventoryItemCommand command);
    
    /**
     * Actualizar un item de inventario existente
     */
    InventoryItem handle(UpdateInventoryItemCommand command);
    
    /**
     * Agregar stock a un item
     */
    InventoryItem handle(AddStockCommand command);
    
    /**
     * Remover stock de un item
     */
    InventoryItem handle(RemoveStockCommand command);
    
    /**
     * Desactivar un item de inventario
     */
    InventoryItem handle(DeactivateInventoryItemCommand command);
} 