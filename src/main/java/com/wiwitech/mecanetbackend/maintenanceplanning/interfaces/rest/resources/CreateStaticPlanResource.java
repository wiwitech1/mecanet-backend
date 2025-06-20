package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateStaticPlanResource(
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,
        
        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate endDate,
        
        @NotNull(message = "El ID de línea de producción es obligatorio")
        @Positive(message = "El ID de línea de producción debe ser positivo")
        Long productionLineId,
        
        @NotNull(message = "El período del ciclo es obligatorio")
        @Min(value = 1, message = "El período del ciclo debe ser al menos 1 día")
        Integer cyclePeriodInDays,
        
        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración debe ser al menos 1 día")
        Integer durationInDays
) {}