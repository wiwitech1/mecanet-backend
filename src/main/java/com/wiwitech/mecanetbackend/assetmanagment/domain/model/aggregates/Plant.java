package com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates;

import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Location;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ContactInfo;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.PlantAlreadyActiveException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.PlantAlreadyInactiveException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.PlantActivatedEvent;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.PlantDeactivatedEvent;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.PlantCreatedEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
@Table(name = "plants")
public class Plant extends AuditableAbstractAggregateRoot<Plant> {
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;
    
    @Embedded
    @NotNull
    private Location location;
    
    @Embedded
    @NotNull
    private ContactInfo contactInfo;
    
    @Column(nullable = false)
    @NotNull
    private Boolean active;
    
    @Embedded
    @AttributeOverride(name = "value",
        column = @Column(name = "tenant_id", nullable = false))
    @NotNull
    private TenantId tenantId;  // ✅ Usando Value Object del Shared Kernel
    
    // ✅ Constructor completo con validaciones
    public Plant(String name, Location location, ContactInfo contactInfo, TenantId tenantId) {
        validateName(name);
        validateLocation(location);
        validateContactInfo(contactInfo);
        validateTenantId(tenantId);
        
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
        this.tenantId = tenantId;
        this.active = true;  // ← Estado inicial por defecto
        
        // ✅ Domain Event al crear
        this.addDomainEvent(new PlantCreatedEvent(this.getId(), name, tenantId.getValue()));
    }
    
    // Constructor vacío para JPA
    protected Plant() {}
    
    // ✅ Factory method
    public static Plant createNew(String name, Location location, ContactInfo contactInfo, TenantId tenantId) {
        return new Plant(name, location, contactInfo, tenantId);
    }
    
    // ✅ Métodos de negocio mejorados con validaciones
    public void activate() {
        if (Boolean.TRUE.equals(this.active)) {
            throw new PlantAlreadyActiveException("Plant " + name + " is already active");
        }
        
        this.active = true;
        this.addDomainEvent(new PlantActivatedEvent(this.getId(), this.name));
    }
    
    public void deactivate() {
        if (Boolean.FALSE.equals(this.active)) {
            throw new PlantAlreadyInactiveException("Plant " + name + " is already inactive");
        }
        
        // if (hasActiveProductionLines()) {
        //     throw new PlantHasActiveProductionLinesException("Cannot deactivate plant with active production lines");
        // }
        
        this.active = false;
        this.addDomainEvent(new PlantDeactivatedEvent(this.getId(), this.name));
    }
    
    // ✅ Métodos de consulta
    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }
    
    public boolean canAddProductionLine() {
        return Boolean.TRUE.equals(this.active);
    }
    
    public String getDisplayName() {
        return name;
    }
    
    // ✅ Métodos para actualización
    public void updateContactInfo(ContactInfo newContactInfo) {
        validateContactInfo(newContactInfo);
        this.contactInfo = newContactInfo;
    }
    
    public void updateLocation(Location newLocation) {
        validateLocation(newLocation);
        this.location = newLocation;
    }
    
    public void updateInfo(String newName, Location newLocation, ContactInfo newContactInfo) {
        validateName(newName);
        validateLocation(newLocation);
        validateContactInfo(newContactInfo);
        
        this.name = newName;
        this.location = newLocation;
        this.contactInfo = newContactInfo;
        
        // Podríamos agregar un evento si es necesario
        // this.addDomainEvent(new PlantUpdatedEvent(...));
    }
    
    // ✅ Validaciones privadas con excepciones específicas
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plant name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Plant name cannot exceed 100 characters");
        }
    }
    
    private void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Plant location cannot be null");
        }
    }
    
    private void validateContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null) {
            throw new IllegalArgumentException("Plant contact info cannot be null");
        }
    }
    
    private void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
    }
}