package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record AddTaskToDynamicPlanResource(
        @NotNull(message = "El ID de máquina es obligatorio")
        @Positive(message = "El ID de máquina debe ser positivo")
        Long machineId,
        
        @NotBlank(message = "El nombre de la tarea es obligatorio")
        String taskName,
        
        String description,
        Set<Long> skillIds
) {}