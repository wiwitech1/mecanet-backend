package com.wiwitech.mecanetbackend.inventory.domain.model.entities;

import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.StockOperationType;
import com.wiwitech.mecanetbackend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que registra todos los movimientos de stock
 */
@Entity
@Table(name = "stock_movements",
    indexes = {
        @Index(name = "idx_inventory_item_id", columnList = "inventory_item_id"),
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_plant_id", columnList = "plant_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_operation_type", columnList = "operation_type"),
        @Index(name = "idx_movement_date", columnList = "movement_date"),
        @Index(name = "idx_sku", columnList = "sku"),
        @Index(name = "idx_machine_id", columnList = "machine_id"),
        @Index(name = "idx_unit_cost", columnList = "unit_cost")
    }
)
@Getter
@NoArgsConstructor
public class StockMovement extends AuditableModel {

    @Column(name = "inventory_item_id", nullable = false)
    private Long inventoryItemId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private StockOperationType operationType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "previous_stock", nullable = false)
    private Integer previousStock;

    @Column(name = "new_stock", nullable = false)
    private Integer newStock;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    // NUEVOS CAMPOS PARA AUDITORÍA MEJORADA
    @Column(name = "unit_cost", precision = 10, scale = 2)
    private BigDecimal unitCost;           // Costo unitario (para operaciones ADD)

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;          // Costo total calculado (quantity * unitCost)

    @Column(name = "machine_id")
    private Long machineId;                // Máquina donde se usó (para operaciones REMOVE)

    public StockMovement(
            Long inventoryItemId,
            String sku,
            StockOperationType operationType,
            Integer quantity,
            Integer previousStock,
            Integer newStock,
            String reason,
            String reference,
            String comments,
            Long userId,
            String userName,
            Long plantId,
            String location,
            Long tenantId,
            // NUEVOS PARÁMETROS
            BigDecimal unitCost,
            Long machineId
    ) {
        this.inventoryItemId = inventoryItemId;
        this.sku = sku;
        this.operationType = operationType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
        this.reference = reference;
        this.comments = comments;
        this.userId = userId;
        this.userName = userName;
        this.plantId = plantId;
        this.location = location;
        this.movementDate = LocalDateTime.now();
        this.tenantId = tenantId;
        
        // NUEVOS CAMPOS
        this.unitCost = unitCost;
        this.totalCost = (unitCost != null && quantity != null) ? 
                        unitCost.multiply(BigDecimal.valueOf(quantity)) : null;
        this.machineId = machineId;
    }
} 