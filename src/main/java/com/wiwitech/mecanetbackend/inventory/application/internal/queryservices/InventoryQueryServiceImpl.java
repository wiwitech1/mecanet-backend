package com.wiwitech.mecanetbackend.inventory.application.internal.queryservices;

import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.queries.*;
import com.wiwitech.mecanetbackend.inventory.domain.services.InventoryQueryService;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.InventoryItemRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación simple del servicio de queries de inventario
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryQueryServiceImpl implements InventoryQueryService {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public Page<InventoryItem> handle(GetAllInventoryItemsQuery query) {
        log.info("Getting all inventory items for tenant with pagination");
        
        TenantId tenantId = requireTenant();
        Page<InventoryItem> items = inventoryItemRepository.findByTenantIdValue(tenantId.getValue(), query.pageable());
        
        log.info("Found {} inventory items for tenant {} (page {})", 
                items.getTotalElements(), tenantId.getValue(), items.getNumber());
        return items;
    }

    @Override
    public Optional<InventoryItem> handle(GetInventoryItemByIdQuery query) {
        log.info("Getting inventory item with ID: {}", query.itemId());
        
        TenantId tenantId = requireTenant();
        Optional<InventoryItem> item = inventoryItemRepository.findByIdAndTenantIdValue(query.itemId(), tenantId.getValue());
        
        if (item.isPresent()) {
            log.info("Found inventory item with ID: {}", query.itemId());
        } else {
            log.warn("Inventory item with ID {} not found for tenant {}", query.itemId(), tenantId.getValue());
        }
        
        return item;
    }

    @Override
    public Optional<InventoryItem> handle(GetInventoryItemBySkuQuery query) {
        log.info("Getting inventory item with SKU: {}", query.sku());
        
        TenantId tenantId = requireTenant();
        Optional<InventoryItem> item = inventoryItemRepository.findBySkuAndTenantIdValue(query.sku(), tenantId.getValue());
        
        if (item.isPresent()) {
            log.info("Found inventory item with SKU: {}", query.sku());
        } else {
            log.warn("Inventory item with SKU {} not found for tenant {}", query.sku(), tenantId.getValue());
        }
        
        return item;
    }

    @Override
    public List<InventoryItem> handle(GetLowStockItemsQuery query) {
        log.info("Getting low stock items for tenant");
        
        TenantId tenantId = requireTenant();
        List<InventoryItem> items = inventoryItemRepository.findLowStockItemsByTenantId(tenantId.getValue());
        
        log.info("Found {} low stock items for tenant {}", items.size(), tenantId.getValue());
        return items;
    }

    @Override
    public List<InventoryItem> handle(GetInventoryItemsByMachineQuery query) {
        log.info("Getting inventory items compatible with machine: {}", query.machineId());
        
        TenantId tenantId = requireTenant();
        // Convertir Long a String para la búsqueda
        String machineIdStr = query.machineId().toString();
        List<InventoryItem> items = inventoryItemRepository.findByCompatibleMachineIdAndTenantIdValue(machineIdStr, tenantId.getValue());
        
        log.info("Found {} compatible items for machine {} in tenant {}", items.size(), query.machineId(), tenantId.getValue());
        return items;
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
} 