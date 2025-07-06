package com.wiwitech.mecanetbackend.inventory.domain.services;

import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.queries.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para queries de inventario
 */
public interface InventoryQueryService {
    
    /**
     * Obtener todos los items de inventario con paginación
     */
    Page<InventoryItem> handle(GetAllInventoryItemsQuery query);
    
    /**
     * Obtener un item por ID
     */
    Optional<InventoryItem> handle(GetInventoryItemByIdQuery query);
    
    /**
     * Obtener un item por SKU
     */
    Optional<InventoryItem> handle(GetInventoryItemBySkuQuery query);
    
    /**
     * Obtener items con stock bajo
     */
    List<InventoryItem> handle(GetLowStockItemsQuery query);
    
    /**
     * Obtener items compatibles con una máquina
     */
    List<InventoryItem> handle(GetInventoryItemsByMachineQuery query);
} 