package com.wiwitech.mecanetbackend.workorders.application.acl.inventory;

import com.wiwitech.mecanetbackend.workorders.interfaces.acl.InventoryContextFacade;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.InventoryItemRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Simplified implementation that directly deducts stock from inventory when work order completes.
 * No reservations or complex workflow - just simple stock deduction.
 */
@Component("workordersInventoryAcl")
@RequiredArgsConstructor
@Slf4j
public class InventoryContextFacadeImpl implements InventoryContextFacade {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    @Transactional
    public void deductStock(WorkOrderId workOrderId, Map<Long, Integer> finalQtyPerItem) {
        log.info("[ACL] Deducting stock for completed WO {} with {} items", 
                workOrderId, finalQtyPerItem.size());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        
        finalQtyPerItem.forEach((itemId, finalQty) -> {
            if (finalQty <= 0) {
                log.warn("[ACL] Skipping item {} - invalid quantity: {}", itemId, finalQty);
                return;
            }
            
            var inventoryItem = inventoryItemRepository.findByIdAndTenantIdValue(itemId, tenantId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory item not found: " + itemId));
            
            // Verificar stock disponible
            if (inventoryItem.getCurrentStock() < finalQty) {
                throw new IllegalArgumentException(
                        "Insufficient stock for item " + itemId + 
                        ": required " + finalQty + ", available " + inventoryItem.getCurrentStock());
            }
            
            // Deducir stock directamente
            inventoryItem.removeStock(finalQty, 
                "Work order completion: " + workOrderId.getValue(), 
                1L, // userId placeholder - could be extracted from context
                workOrderId.toString(), 
                null); // machineId - could be extracted from work order
            inventoryItemRepository.save(inventoryItem);
            
            log.info("[ACL] Deducted {} units of item {} for completed WO {}", 
                    finalQty, itemId, workOrderId.getValue());
        });
    }
} 