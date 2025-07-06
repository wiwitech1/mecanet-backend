package com.wiwitech.mecanetbackend.inventory.interfaces.rest;

import com.wiwitech.mecanetbackend.inventory.domain.model.entities.StockMovement;
import com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories.StockMovementRepository;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources.StockMovementResource;
import com.wiwitech.mecanetbackend.inventory.interfaces.rest.transform.StockMovementResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock-movements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stock Movements", description = "Gestión de movimientos de stock")
public class StockMovementsController {

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementResourceFromEntityAssembler stockMovementResourceFromEntityAssembler;

    @GetMapping("/item/{itemId}")
    @Operation(summary = "Movimientos de stock por item", description = "Obtiene el historial de movimientos de stock para un item específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<Page<StockMovementResource>> getStockMovementsByItem(
            @Parameter(description = "ID del item") @PathVariable Long itemId,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        log.info("Obteniendo movimientos de stock para item: {} - página: {}, tamaño: {}", itemId, page, size);
        
        if (!TenantContext.hasTenant()) {
            throw new IllegalStateException("Tenant context is required");
        }
        
        TenantId tenantId = new TenantId(TenantContext.getCurrentTenantId());
        Pageable pageable = PageRequest.of(page, size);
        
        Page<StockMovement> movements = stockMovementRepository.findByInventoryItemIdAndTenantIdOrderByMovementDateDesc(itemId, tenantId.getValue(), pageable);
        
        Page<StockMovementResource> response = movements.map(stockMovementResourceFromEntityAssembler::toResource);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    @Operation(summary = "Movimientos recientes", description = "Obtiene los movimientos de stock más recientes del tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos obtenidos exitosamente")
    })
    public ResponseEntity<Page<StockMovementResource>> getRecentStockMovements(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        log.info("Obteniendo movimientos recientes - página: {}, tamaño: {}", page, size);
        
        if (!TenantContext.hasTenant()) {
            throw new IllegalStateException("Tenant context is required");
        }
        
        TenantId tenantId = new TenantId(TenantContext.getCurrentTenantId());
        Pageable pageable = PageRequest.of(page, size);
        
        Page<StockMovement> movements = stockMovementRepository.findByTenantIdOrderByCreatedAtDesc(tenantId.getValue(), pageable);
        
        Page<StockMovementResource> response = movements.map(stockMovementResourceFromEntityAssembler::toResource);
        return ResponseEntity.ok(response);
    }
} 