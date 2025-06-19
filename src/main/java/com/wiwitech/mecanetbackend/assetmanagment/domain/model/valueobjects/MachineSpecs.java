package com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Embeddable
@Getter
public class MachineSpecs {
    @Size(max = 50)
    private String manufacturer;
    
    @Size(max = 50)
    private String model;
    
    @Size(max = 50)
    private String type;
    
    private Double powerConsumption;
    
    // Constructor completo
    public MachineSpecs(String manufacturer, String model, String type, Double powerConsumption) {
        validateManufacturer(manufacturer);
        validateModel(model);
        validateType(type);
        validatePowerConsumption(powerConsumption);
        
        this.manufacturer = manufacturer;
        this.model = model;
        this.type = type;
        this.powerConsumption = powerConsumption;
    }
    
    // Constructor vacío para JPA
    protected MachineSpecs() {}
    
    // Validaciones privadas
    private void validateManufacturer(String manufacturer) {
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer cannot be null or empty");
        }
        if (manufacturer.length() > 50) {
            throw new IllegalArgumentException("Manufacturer cannot exceed 50 characters");
        }
    }
    
    private void validateModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty");
        }
        if (model.length() > 50) {
            throw new IllegalArgumentException("Model cannot exceed 50 characters");
        }
    }
    
    private void validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        if (type.length() > 50) {
            throw new IllegalArgumentException("Type cannot exceed 50 characters");
        }
    }
    
    private void validatePowerConsumption(Double powerConsumption) {
        if (powerConsumption != null && powerConsumption <= 0) {
            throw new IllegalArgumentException("Power consumption must be positive");
        }
    }
}