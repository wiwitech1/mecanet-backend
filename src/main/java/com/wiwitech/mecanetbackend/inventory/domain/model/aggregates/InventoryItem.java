package com.wiwitech.mecanetbackend.inventory.domain.model.aggregates;

import com.wiwitech.mecanetbackend.inventory.domain.model.events.*;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aggregate Root principal para la gestión de inventario
 */
@Entity
@Table(name = "inventory_items", 
    indexes = {
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_sku", columnList = "sku"),
        @Index(name = "idx_current_stock", columnList = "current_stock"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_plant_id", columnList = "plant_id"),
        @Index(name = "idx_expiration_date", columnList = "expiration_date")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sku_tenant", columnNames = {"sku", "tenant_id"})
    }
)
@Getter
@NoArgsConstructor
public class InventoryItem extends AuditableAbstractAggregateRoot<InventoryItem> {

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "part_number", length = 100)
    private String partNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ItemCategory category;

    @Column(name = "sub_category", length = 100)
    private String subCategory;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(name = "zone", length = 100)
    private String zone;

    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "area", length = 100)
    private String area;

    @Column(name = "location_code", length = 20)
    private String locationCode;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;

    @Column(name = "maximum_stock")
    private Integer maximumStock;

    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;

    @Column(name = "unit_cost", precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "supplier_name", length = 200)
    private String supplierName;

    @Column(name = "supplier_contact", length = 200)
    private String supplierContact;

    @Column(name = "supplier_phone", length = 20)
    private String supplierPhone;

    @Column(name = "supplier_email", length = 200)
    private String supplierEmail;

    @Column(name = "supplier_code", length = 50)
    private String supplierCode;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "last_restock_date")
    private LocalDate lastRestockDate;

    @Column(name = "last_audit_date")
    private LocalDate lastAuditDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    private ItemCondition condition;

    @Column(name = "compatible_machines", columnDefinition = "TEXT")
    private String compatibleMachines;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "tenant_id"))
    private TenantId tenantId;

    public InventoryItem(
            String sku,
            String name,
            String description,
            String brand,
            String model,
            String partNumber,
            ItemCategory category,
            String subCategory,
            Long plantId,
            String zone,
            String building,
            String area,
            String locationCode,
            Integer initialStock,
            Integer minimumStock,
            Integer maximumStock,
            String unitOfMeasure,
            BigDecimal unitCost,
            String currency,
            String supplierName,
            String supplierContact,
            String supplierPhone,
            String supplierEmail,
            String supplierCode,
            LocalDate expirationDate,
            String compatibleMachines,
            TenantId tenantId
    ) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.partNumber = partNumber;
        this.category = category;
        this.subCategory = subCategory;
        this.plantId = plantId;
        this.zone = zone;
        this.building = building;
        this.area = area;
        this.locationCode = locationCode;
        this.currentStock = initialStock != null ? initialStock : 0;
        this.minimumStock = minimumStock != null ? minimumStock : 0;
        this.maximumStock = maximumStock;
        this.unitOfMeasure = unitOfMeasure;
        this.unitCost = unitCost;
        this.currency = currency;
        this.supplierName = supplierName;
        this.supplierContact = supplierContact;
        this.supplierPhone = supplierPhone;
        this.supplierEmail = supplierEmail;
        this.supplierCode = supplierCode;
        this.expirationDate = expirationDate;
        this.lastRestockDate = LocalDate.now();
        this.status = ItemStatus.ACTIVE;
        this.condition = ItemCondition.NEW;
        this.compatibleMachines = compatibleMachines;
        this.tenantId = tenantId;

        // Emitir evento de creación
        addDomainEvent(new InventoryItemCreatedEvent(
            this.getId(),
            this.sku,
            this.name,
            this.category.name(),
            this.plantId,
            this.tenantId.getValue()
        ));
    }

    /**
     * Agregar stock al item
     */
    public void addStock(Integer quantity, String reason, Long userId, BigDecimal unitCost, String reference) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        Integer previousStock = this.currentStock;
        this.currentStock += quantity;
        this.lastRestockDate = LocalDate.now();

        // Emitir evento con información extendida
        addDomainEvent(new StockAddedEvent(
            this.getId(),
            this.sku,
            this.name,
            quantity,
            previousStock,
            this.currentStock,
            reason,
            userId,
            this.plantId,
            this.tenantId.getValue(),
            unitCost,
            reference
        ));
    }

    /**
     * Remover stock del item
     */
    public void removeStock(Integer quantity, String reason, Long userId, String reference, Long machineId) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        if (this.currentStock < quantity) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + this.currentStock + ", Solicitado: " + quantity);
        }

        Integer previousStock = this.currentStock;
        this.currentStock -= quantity;

        // Emitir evento con información extendida
        addDomainEvent(new StockRemovedEvent(
            this.getId(),
            this.sku,
            this.name,
            quantity,
            previousStock,
            this.currentStock,
            reason,
            userId,
            this.plantId,
            this.tenantId.getValue(),
            reference,
            machineId
        ));

        // Verificar si necesita reposición
        if (this.currentStock <= this.minimumStock) {
            addDomainEvent(new LowStockAlertEvent(
                this.getId(),
                this.sku,
                this.name,
                this.currentStock,
                this.minimumStock,
                this.plantId,
                this.tenantId.getValue()
            ));
        }
    }

    /**
     * Ajustar stock a una cantidad específica
     */
    public void adjustStock(Integer newQuantity, String reason, Long userId) {
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Integer previousStock = this.currentStock;
        this.currentStock = newQuantity;

        // Emitir evento de ajuste
        addDomainEvent(new StockRemovedEvent(
            this.getId(),
            this.sku,
            this.name,
            previousStock - newQuantity,
            previousStock,
            this.currentStock,
            reason,
            userId,
            this.plantId,
            this.tenantId.getValue(),
            "ADJUSTMENT",  // reference
            null          // machineId
        ));
    }

    /**
     * Verificar si necesita reposición
     */
    public boolean needsRestock() {
        return this.currentStock <= this.minimumStock;
    }

    /**
     * Verificar si está vencido
     */
    public boolean isExpired() {
        return this.expirationDate != null && this.expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Verificar si tiene stock suficiente
     */
    public boolean hasStock(Integer quantity) {
        return this.currentStock >= quantity;
    }

    /**
     * Actualizar ubicación
     */
    public void updateLocation(String newZone, String newBuilding, String newArea, String newLocationCode) {
        this.zone = newZone;
        this.building = newBuilding;
        this.area = newArea;
        this.locationCode = newLocationCode;
    }

    /**
     * Actualizar información del proveedor
     */
    public void updateSupplier(String supplierName, String supplierContact, String supplierPhone, String supplierEmail, String supplierCode) {
        this.supplierName = supplierName;
        this.supplierContact = supplierContact;
        this.supplierPhone = supplierPhone;
        this.supplierEmail = supplierEmail;
        this.supplierCode = supplierCode;
    }

    /**
     * Desactivar item
     */
    public void deactivate() {
        this.status = ItemStatus.INACTIVE;
    }

    /**
     * Reactivar item
     */
    public void reactivate() {
        this.status = ItemStatus.ACTIVE;
    }

    /**
     * Actualizar información básica
     */
    public void updateBasicInfo(String name, String description, String brand, String model, String partNumber) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.partNumber = partNumber;
    }

    /**
     * Actualizar configuración de stock
     */
    public void updateStockConfiguration(Integer minimumStock, Integer maximumStock, String unitOfMeasure) {
        this.minimumStock = minimumStock;
        this.maximumStock = maximumStock;
        this.unitOfMeasure = unitOfMeasure;
    }

    /**
     * Actualizar costo
     */
    public void updateCost(BigDecimal unitCost, String currency) {
        this.unitCost = unitCost;
        this.currency = currency;
    }

    /**
     * Actualizar compatibilidad con máquinas
     */
    public void updateCompatibleMachines(String compatibleMachines) {
        this.compatibleMachines = compatibleMachines;
    }

    /**
     * Actualizar fecha de vencimiento
     */
    public void updateExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Registrar auditoría
     */
    public void recordAudit(LocalDate auditDate) {
        this.lastAuditDate = auditDate;
    }
} 