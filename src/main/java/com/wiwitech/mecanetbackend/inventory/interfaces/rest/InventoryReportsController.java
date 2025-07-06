package com.wiwitech.mecanetbackend.inventory.interfaces.rest;

import com.wiwitech.mecanetbackend.inventory.application.internal.queryservices.InventoryQueryServiceImpl;
import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.queries.GetLowStockItemsQuery;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.InventoryItemRepository;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.StockMovementRepository;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.InventorySummaryResource;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory-reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Reports", description = "Reportes de inventario")
public class InventoryReportsController {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryQueryServiceImpl inventoryQueryService;

    @GetMapping("/summary")
    @Operation(summary = "Resumen de inventario", description = "Obtiene un resumen general del inventario del tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente")
    })
    public ResponseEntity<InventorySummaryResource> getInventorySummary() {
        log.info("Generando resumen de inventario");
        
        if (!TenantContext.hasTenant()) {
            throw new IllegalStateException("Tenant context is required");
        }
        
        TenantId tenantId = new TenantId(TenantContext.getCurrentTenantId());
        
        // Obtener estadísticas
        long totalItems = inventoryItemRepository.countByTenantIdValue(tenantId.getValue());
        long activeItems = inventoryItemRepository.countByStatusAndTenantIdValue(
            com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemStatus.ACTIVE, 
            tenantId.getValue()
        );
        long lowStockItems = inventoryItemRepository.countLowStockItemsByTenantId(tenantId.getValue());
        
        // Obtener valor total del inventario (aproximado)
        List<InventoryItem> items = inventoryItemRepository.findByTenantIdValue(tenantId.getValue());
        BigDecimal totalValue = items.stream()
            .filter(item -> item.getUnitCost() != null && item.getCurrentStock() != null)
            .map(item -> item.getUnitCost().multiply(BigDecimal.valueOf(item.getCurrentStock())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Obtener movimientos recientes - usar countByTenantId
        long recentMovements = stockMovementRepository.countByTenantId(tenantId.getValue());
        
        InventorySummaryResource summary = new InventorySummaryResource(
            totalItems,
            activeItems,
            lowStockItems,
            totalValue,
            recentMovements
        );
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/low-stock-count")
    @Operation(summary = "Conteo de items con stock bajo", description = "Obtiene el número de items que están por debajo del stock mínimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    })
    public ResponseEntity<Long> getLowStockCount() {
        log.info("Obteniendo conteo de items con stock bajo");
        
        if (!TenantContext.hasTenant()) {
            throw new IllegalStateException("Tenant context is required");
        }
        
        TenantId tenantId = new TenantId(TenantContext.getCurrentTenantId());
        long count = inventoryItemRepository.countLowStockItemsByTenantId(tenantId.getValue());
        
        return ResponseEntity.ok(count);
    }
} 