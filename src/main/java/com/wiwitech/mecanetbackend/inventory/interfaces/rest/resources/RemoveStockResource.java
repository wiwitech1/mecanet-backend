package com.wiwitech.mecanetbackend.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RemoveStockResource(
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    Integer quantity,
    
    @NotBlank(message = "La razón es obligatoria")
    String reason,
    
    // NUEVOS CAMPOS OPCIONALES PARA AUDITORÍA
    String reference,       // Referencia externa (work order, ticket, etc.)
    
    Long machineId         // ID de la máquina donde se usó el repuesto
) {} 