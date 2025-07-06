package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemToStaticPlanResource(
        @NotNull(message = "El índice del día es obligatorio")
        @Min(value = 1, message = "El índice del día debe ser al menos 1")
        Integer dayIndex
) {}