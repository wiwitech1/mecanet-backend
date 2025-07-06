package com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Resource for updating an existing plan
 */
public record UpdatePlanResource(
        @NotBlank(message = "Plan description cannot be blank")
        String description,
        
        @NotNull(message = "Plan cost cannot be null")
        @PositiveOrZero(message = "Plan cost cannot be negative")
        BigDecimal cost
) {} 