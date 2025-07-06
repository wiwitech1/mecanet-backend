package com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record CreateInventoryItemResource(
    @NotBlank(message = "El SKU es obligatorio")
    @Size(min = 3, max = 50, message = "El SKU debe tener entre 3 y 50 caracteres")
    String sku,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String name,
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    String description,
    
    @NotBlank(message = "La categoría es obligatoria")
    @Size(min = 2, max = 50, message = "La categoría debe tener entre 2 y 50 caracteres")
    String category,
    
    @NotBlank(message = "La unidad es obligatoria")
    @Size(min = 1, max = 20, message = "La unidad debe tener entre 1 y 20 caracteres")
    String unit,
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser positivo")
    BigDecimal unitPrice,
    
    @NotNull(message = "El stock mínimo es obligatorio")
    @Positive(message = "El stock mínimo debe ser positivo")
    Integer minimumStock,
    
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    String location,
    
    @NotNull(message = "El ID de la planta es obligatorio")
    @Positive(message = "El ID de la planta debe ser positivo")
    Long plantId,
    
    Set<Long> compatibleMachineIds
) {} 