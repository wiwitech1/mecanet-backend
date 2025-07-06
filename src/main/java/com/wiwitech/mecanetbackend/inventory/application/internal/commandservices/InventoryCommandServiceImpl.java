package com.wiwitech.mecanetbackend.inventory.application.internal.commandservices;

import com.wiwitech.mecanetbackend.inventory.application.internal.outboundservices.PlantCatalogAcl;
import com.wiwitech.mecanetbackend.inventory.domain.exceptions.InventoryItemNotFoundException;
import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.commands.*;
import com.wiwitech.mecanetbackend.inventory.domain.services.InventoryCommandService;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.InventoryItemRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación simple del servicio de comandos de inventario
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCommandServiceImpl implements InventoryCommandService {

    private final InventoryItemRepository inventoryItemRepository;
    private final PlantCatalogAcl plantCatalogAcl;

    @Override
    @Transactional
    public InventoryItem handle(CreateInventoryItemCommand command) {
        log.info("Creating inventory item with SKU: {}", command.sku());
        
        // Validar tenant context
        TenantId tenantId = requireTenant();
        
        // Validar que la planta existe
        if (!plantCatalogAcl.plantExists(command.plantId(), tenantId.getValue())) {
            throw new IllegalArgumentException("Plant with ID " + command.plantId() + " does not exist");
        }
        
        // Validar SKU único
        if (inventoryItemRepository.existsBySkuAndTenantIdValue(command.sku(), tenantId.getValue())) {
            throw new IllegalArgumentException("SKU " + command.sku() + " already exists");
        }
        
        // Crear el item
        InventoryItem item = new InventoryItem(
            command.sku(),
            command.name(),
            command.description(),
            command.brand(),
            command.model(),
            command.partNumber(),
            command.category(),
            command.subCategory(),
            command.plantId(),
            command.zone(),
            command.building(),
            command.area(),
            command.locationCode(),
            command.initialStock(),
            command.minimumStock(),
            command.maximumStock(),
            command.unitOfMeasure(),
            command.unitCost(),
            command.currency(),
            command.supplierName(),
            command.supplierContact(),
            command.supplierPhone(),
            command.supplierEmail(),
            command.supplierCode(),
            command.expirationDate(),
            command.compatibleMachines(),
            tenantId
        );
        
        InventoryItem savedItem = inventoryItemRepository.save(item);
        log.info("Created inventory item with ID: {}", savedItem.getId());
        
        return savedItem;
    }

    @Override
    @Transactional
    public InventoryItem handle(UpdateInventoryItemCommand command) {
        log.info("Updating inventory item with ID: {}", command.itemId());
        
        // Validar tenant context
        TenantId tenantId = requireTenant();
        
        // Buscar el item
        InventoryItem item = loadItemOrThrow(command.itemId(), tenantId);
        
        // Actualizar información básica
        item.updateBasicInfo(
            command.name(),
            command.description(),
            command.brand(),
            command.model(),
            null // partNumber no se actualiza en este comando
        );
        
        // Actualizar ubicación
        item.updateLocation(
            command.zone(),
            command.building(),
            command.area(),
            command.locationCode()
        );
        
        // Actualizar configuración de stock
        item.updateStockConfiguration(
            command.minimumStock(),
            command.maximumStock(),
            command.unitOfMeasure() // usar el valor del command en lugar de null
        );
        
        // Actualizar costo
        item.updateCost(command.unitCost(), command.currency());
        
        // Actualizar información del proveedor
        item.updateSupplier(
            command.supplierName(),
            command.supplierContact(),
            command.supplierPhone(),
            command.supplierEmail(),
            null // supplierCode no se actualiza en este comando
        );
        
        // Actualizar compatibilidad con máquinas
        item.updateCompatibleMachines(command.compatibleMachines());
        
        InventoryItem savedItem = inventoryItemRepository.save(item);
        log.info("Updated inventory item with ID: {}", savedItem.getId());
        
        return savedItem;
    }

    @Override
    @Transactional
    public InventoryItem handle(AddStockCommand command) {
        log.info("Adding stock to item with ID: {}", command.itemId());
        
        // Validar tenant context
        TenantId tenantId = requireTenant();
        
        // Buscar el item
        InventoryItem item = loadItemOrThrow(command.itemId(), tenantId);
        
        // Agregar stock con información extendida
        item.addStock(
            command.quantity(), 
            command.reason(), 
            command.userId(),
            command.unitCost(),    // Nuevo parámetro
            command.reference()    // Nuevo parámetro
        );
        
        // Guardar cambios
        InventoryItem savedItem = inventoryItemRepository.save(item);
        
        log.info("Stock added successfully to item: {}", savedItem.getSku());
        return savedItem;
    }

    @Override
    @Transactional
    public InventoryItem handle(RemoveStockCommand command) {
        log.info("Removing stock from item with ID: {}", command.itemId());
        
        // Validar tenant context
        TenantId tenantId = requireTenant();
        
        // Buscar el item
        InventoryItem item = loadItemOrThrow(command.itemId(), tenantId);
        
        // Remover stock con información extendida
        item.removeStock(
            command.quantity(), 
            command.reason(), 
            command.userId(),
            command.reference(),   // Nuevo parámetro
            command.machineId()    // Nuevo parámetro
        );
        
        // Guardar cambios
        InventoryItem savedItem = inventoryItemRepository.save(item);
        
        log.info("Stock removed successfully from item: {}", savedItem.getSku());
        return savedItem;
    }

    @Override
    @Transactional
    public InventoryItem handle(DeactivateInventoryItemCommand command) {
        log.info("Deactivating inventory item with ID: {}", command.itemId());
        
        // Validar tenant context
        TenantId tenantId = requireTenant();
        
        // Buscar el item
        InventoryItem item = loadItemOrThrow(command.itemId().value(), tenantId);
        
        // Desactivar el item
        item.deactivate();
        
        InventoryItem savedItem = inventoryItemRepository.save(item);
        log.info("Deactivated inventory item with ID: {}", savedItem.getId());
        
        return savedItem;
    }

    /**
     * Helper para validar tenant context
     */
    private TenantId requireTenant() {
        if (!TenantContext.hasTenant()) {
            throw new IllegalStateException("Tenant context is required");
        }
        return new TenantId(TenantContext.getCurrentTenantId());
    }

    /**
     * Helper para cargar item o lanzar excepción
     */
    private InventoryItem loadItemOrThrow(Long itemId, TenantId tenantId) {
        return inventoryItemRepository.findByIdAndTenantIdValue(itemId, tenantId.getValue())
            .orElseThrow(() -> new InventoryItemNotFoundException("Inventory item with ID " + itemId + " not found"));
    }
} 