package com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Capacity;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ProductionLineStatus;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.ProductionLineNotReadyException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.ProductionLineAlreadyRunningException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.ProductionLineAlreadyStoppedException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.ProductionLineInMaintenanceException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.ProductionLineCreatedEvent;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.ProductionStartedEvent;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.ProductionStoppedEvent;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "production_lines")
public class ProductionLine extends AuditableAbstractAggregateRoot<ProductionLine> {
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;
    
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String code;  // ← Código único por planta
    
    @Embedded
    @NotNull
    private Capacity capacity;  // ← unidades/hora
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductionLineStatus status;
    
    @NotNull
    private Long plantId;  // ← Referencia a Plant (otro aggregate)
    
    @Embedded
    @NotNull
    @AttributeOverride(name = "value",
        column = @Column(name = "tenant_id", nullable = false))
    private TenantId tenantId;  // ✅ Usando Value Object del Shared Kernel
    
    // ✅ Constructor completo con validaciones
    public ProductionLine(String name, String code, Capacity capacity, Long plantId, TenantId tenantId) {
        validateName(name);
        validateCode(code);
        validateCapacity(capacity);
        validatePlantId(plantId);
        validateTenantId(tenantId);
        
        this.name = name;
        this.code = code;
        this.capacity = capacity;
        this.plantId = plantId;
        this.tenantId = tenantId;
        this.status = ProductionLineStatus.READY;  // ← Estado inicial
        
        // ✅ Domain Event al crear
        this.addDomainEvent(new ProductionLineCreatedEvent(
            this.getId(), 
            name, 
            plantId, 
            tenantId.getValue()
        ));
    }
    public void stopProduction(String reason) {
        validateCanStopProduction();
        this.status = ProductionLineStatus.STOPPED;
        this.addDomainEvent(new ProductionStoppedEvent(
            this.getId(), 
            this.name, 
            this.plantId, 
            reason
        ));
    }
    
    // Constructor vacío para JPA
    protected ProductionLine() {}
    
    // ✅ Factory method
    public static ProductionLine createNew(String name, String code, Capacity capacity, Long plantId, TenantId tenantId) {
        return new ProductionLine(name, code, capacity, plantId, tenantId);
    }
    
    // ✅ Métodos de negocio mejorados con validaciones
    public void startProduction() {
        validateCanStartProduction();
        
        ProductionLineStatus previousStatus = this.status;
        this.status = ProductionLineStatus.RUNNING;
        
        this.addDomainEvent(new ProductionStartedEvent(
            this.getId(), 
            this.name, 
            this.plantId, 
            previousStatus
        ));
    }
    
    public void stopProduction() {
        validateCanStopProduction();
        ProductionLineStatus previousStatus = this.status;
        this.status = ProductionLineStatus.STOPPED;
        
        this.addDomainEvent(new ProductionStoppedEvent(
            this.getId(), 
            this.name, 
            this.plantId, 
            "Manual stop"
        ));
    }
    
    public void putInMaintenance() {
        if (this.status == ProductionLineStatus.RUNNING) {
            throw new ProductionLineInMaintenanceException(
                "Cannot put production line " + name + " in maintenance while running"
            );
        }
        
        this.status = ProductionLineStatus.MAINTENANCE;
    }
    
    public void markAsReady() {
        this.status = ProductionLineStatus.READY;
    }
    
    // ✅ Métodos de consulta
    public boolean isRunning() {
        return status == ProductionLineStatus.RUNNING;
    }
    
    public boolean isReady() {
        return status == ProductionLineStatus.READY;
    }
    
    public boolean canAddMachine() {
        return status != ProductionLineStatus.MAINTENANCE && status != ProductionLineStatus.RUNNING;
    }
    
    public boolean canStartProduction() {
        return status == ProductionLineStatus.READY;
    }
    
    public String getDisplayName() {
        return name + " (" + code + ")";
    }
    
    // ✅ Métodos para actualización
    public void updateCapacity(Capacity newCapacity) {
        validateCapacity(newCapacity);
        this.capacity = newCapacity;
    }
    
    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }
    
    // ✅ Validaciones privadas con excepciones específicas
    private void validateCanStartProduction() {
        if (this.status == null) {
            throw new IllegalStateException("Production line status is not initialized");
        }
        if (this.status == ProductionLineStatus.RUNNING) {
            throw new ProductionLineAlreadyRunningException("Production line " + name + " is already running");
        }
        if (this.status != ProductionLineStatus.READY) {
            throw new ProductionLineNotReadyException(
                "Cannot start production on line " + name + ". Current status: " + status + 
                ". Required status: " + ProductionLineStatus.READY
            );
        }
        if (capacity == null || !capacity.isValid()) {
            throw new IllegalStateException("Production line capacity is not properly configured");
        }
    }
    
    private void validateCanStopProduction() {
        if (this.status == ProductionLineStatus.STOPPED) {
            throw new ProductionLineAlreadyStoppedException("Production line " + name + " is already stopped");
        }
        if (this.status != ProductionLineStatus.RUNNING) {
            throw new IllegalStateException("Cannot stop production line that is not running");
        }
    }
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Production line name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Production line name cannot exceed 100 characters");
        }
    }
    
    private void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Production line code cannot be null or empty");
        }
        if (code.length() > 50) {
            throw new IllegalArgumentException("Production line code cannot exceed 50 characters");
        }
    }
    
    private void validateCapacity(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException("Production line capacity cannot be null");
        }
    }
    
    private void validatePlantId(Long plantId) {
        if (plantId == null) {
            throw new IllegalArgumentException("Plant ID cannot be null");
        }
    }
    
    private void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
    }
}