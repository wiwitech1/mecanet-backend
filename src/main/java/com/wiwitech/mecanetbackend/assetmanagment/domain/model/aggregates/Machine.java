package com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates;

import java.time.LocalDateTime;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MachineSpecs;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MachineStatus;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MaintenanceInfo;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.MachineInMaintenanceException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.MachineNotOperationalException;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.MachineAssignedEvent;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.MachineMaintenanceStartedEvent;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.MachineMaintenanceCompletedEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "machines")
public class Machine extends AuditableAbstractAggregateRoot<Machine> {
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false)
    private String serialNumber;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Embedded
    @NotNull
    private MachineSpecs specs;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private MachineStatus status;
    
    @Embedded
    @NotNull
    private MaintenanceInfo maintenanceInfo;
    
    private Long productionLineId;
    
    @Embedded
    @AttributeOverride(name = "value",
    column = @Column(name = "tenant_id", nullable = false))
    @NotNull
    private TenantId tenantId;  // ✅ Usando Value Object del Shared Kernel
    
    // ✅ Constructor completo con validaciones
    public Machine(String serialNumber, String name, MachineSpecs specs, TenantId tenantId) {
        validateSerialNumber(serialNumber);
        validateName(name);
        validateSpecs(specs);
        validateTenantId(tenantId);
        
        this.serialNumber = serialNumber;
        this.name = name;
        this.specs = specs;
        this.tenantId = tenantId;
        this.status = MachineStatus.OPERATIONAL;
        this.maintenanceInfo = MaintenanceInfo.createNew();
    }
    
    // Constructor vacío para JPA
    protected Machine() {}
    
    // ✅ Factory method
    public static Machine createNew(String serialNumber, String name, MachineSpecs specs, TenantId tenantId) {
        return new Machine(serialNumber, name, specs, tenantId);
    }
    
    // ✅ Métodos de negocio mejorados
    public void assignToProductionLine(Long productionLineId) {
        validateCanBeAssigned();
        this.productionLineId = productionLineId;
        this.addDomainEvent(new MachineAssignedEvent(this.getId(), productionLineId));
    }
    
    public void startMaintenance() {
        validateCanStartMaintenance();
        MachineStatus previousStatus = this.status;
        this.status = MachineStatus.IN_MAINTENANCE;
        this.maintenanceInfo = this.maintenanceInfo.withLastMaintenanceDate(LocalDateTime.now());
        this.addDomainEvent(new MachineMaintenanceStartedEvent(this.getId(), previousStatus));
    }
    
    public void completeMaintenance() {
        if (this.status != MachineStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Machine is not in maintenance");
        }
        this.status = MachineStatus.OPERATIONAL;
        this.maintenanceInfo = this.maintenanceInfo.withNextMaintenanceDate();
        this.addDomainEvent(new MachineMaintenanceCompletedEvent(this.getId()));
    }
    
    // ✅ Métodos de consulta
    public boolean needsMaintenance() {
        return maintenanceInfo.isMaintenanceDue();
    }
    
    public boolean isOperational() {
        return status == MachineStatus.OPERATIONAL;
    }
    
    public boolean isAvailableForAssignment() {
        return status == MachineStatus.OPERATIONAL && productionLineId == null;
    }
    
    // ✅ Validaciones privadas con excepciones específicas
    private void validateCanBeAssigned() {
        if (this.status == MachineStatus.IN_MAINTENANCE) {
            throw new MachineInMaintenanceException("Cannot assign machine " + serialNumber + " while in maintenance");
        }
        if (this.status != MachineStatus.OPERATIONAL) {
            throw new MachineNotOperationalException("Machine " + serialNumber + " is not operational");
        }
    }
    
    private void validateCanStartMaintenance() {
        if (this.status == MachineStatus.IN_MAINTENANCE) {
            throw new MachineInMaintenanceException("Machine " + serialNumber + " is already in maintenance");
        }
    }
    
    private void validateSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Serial number cannot be null or empty");
        }
    }
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Machine name cannot be null or empty");
        }
    }
    
    private void validateSpecs(MachineSpecs specs) {
        if (specs == null) {
            throw new IllegalArgumentException("Machine specs cannot be null");
        }
    }
    
    private void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
    }
}