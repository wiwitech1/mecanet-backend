package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateDynamicPlanResource(
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,
        
        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate endDate,
        
        @NotNull(message = "El ID de métrica es obligatorio")
        @Positive(message = "El ID de métrica debe ser positivo")
        Long metricDefinitionId,
        
        @NotNull(message = "El umbral es obligatorio")
        @Positive(message = "El umbral debe ser positivo")
        Double threshold
) {}