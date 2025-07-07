package com.wiwitech.mecanetbackend.workorders.application.acl.inventory;

import com.wiwitech.mecanetbackend.workorders.interfaces.acl.InventoryContextFacade;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.UpdateMaterialsCommand;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.InventoryItemRepository;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.StockReservationRepository;
import com.wiwitech.mecanetbackend.inventory.domain.model.entities.StockReservation;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ReservationStatus;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Real implementation that interacts with Inventory BC repositories
 */
@Component("workordersInventoryAcl")
@RequiredArgsConstructor
@Slf4j
public class InventoryContextFacadeImpl implements InventoryContextFacade {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockReservationRepository stockReservationRepository;

    @Override
    @Transactional
    public void reserveMaterials(WorkOrderId workOrderId, Set<UpdateMaterialsCommand.MaterialLine> lines) {
        log.info("[ACL] Reserving materials for WO {} -> {} lines", workOrderId, lines.size());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        
        for (UpdateMaterialsCommand.MaterialLine line : lines) {
            // Buscar el item de inventario
            var inventoryItem = inventoryItemRepository.findByIdAndTenantIdValue(line.itemId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory item not found: " + line.itemId()));
            
            // Verificar stock disponible
            if (inventoryItem.getCurrentStock() < line.quantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for item " + line.itemId() + 
                        ": required " + line.quantity() + ", available " + inventoryItem.getCurrentStock());
            }
            
            // Crear reserva
            StockReservation reservation = new StockReservation(
                    line.itemId(),
                    line.sku(),
                    line.quantity(),
                    inventoryItem.getCurrentStock(),
                    "WORK_ORDER",
                    workOrderId.toString(),
                    "Material reservation for work order: " + workOrderId.getValue(),
                    1L, // userId placeholder
                    "System",
                    LocalDateTime.now().plusDays(7), // expira en 7 días
                    tenantId
            );
            
            stockReservationRepository.save(reservation);
            log.info("[ACL] Reserved {} units of item {} for WO {}", 
                    line.quantity(), line.itemId(), workOrderId.getValue());
        }
    }

    @Override
    @Transactional
    public void consumeReservations(WorkOrderId workOrderId) {
        log.info("[ACL] Consuming reservations for WO {}", workOrderId);
        
        Long tenantId = TenantContext.getCurrentTenantId();
        
        // Buscar todas las reservas activas para esta orden de trabajo
        List<StockReservation> reservations = stockReservationRepository
                .findByReservationTypeAndTenantId("WORK_ORDER", tenantId);
        
        reservations.stream()
                .filter(r -> r.getReference().equals(workOrderId.toString()) && 
                           r.getStatus() == ReservationStatus.ACTIVE)
                .forEach(reservation -> {
                    reservation.release();
                    stockReservationRepository.save(reservation);
                    log.info("[ACL] Released reservation {} for WO {}", 
                            reservation.getId(), workOrderId.getValue());
                });
    }

    @Override
    @Transactional
    public void finalizeConsumptions(WorkOrderId workOrderId, Map<Long, Integer> finalQtyPerItem) {
        log.info("[ACL] Finalizing consumptions for WO {} with {} items", 
                workOrderId, finalQtyPerItem.size());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        
        // Por simplicidad, solo registramos las cantidades finales
        // En una implementación completa, aquí se ajustarían las diferencias
        // entre cantidades reservadas y realmente consumidas
        finalQtyPerItem.forEach((itemId, finalQty) -> {
            log.info("[ACL] Final consumption for item {}: {} units", itemId, finalQty);
        });
    }
} 