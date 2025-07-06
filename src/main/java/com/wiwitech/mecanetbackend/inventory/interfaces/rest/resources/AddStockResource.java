package com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddStockResource(
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    Integer quantity,
    
    @NotBlank(message = "La razón es obligatoria")
    String reason,
    
    // NUEVOS CAMPOS OPCIONALES PARA AUDITORÍA
    @Positive(message = "El costo unitario debe ser positivo")
    BigDecimal unitCost,    // Costo unitario de esta compra específica
    
    String reference        // Referencia externa (factura, orden de compra, etc.)
) {} 