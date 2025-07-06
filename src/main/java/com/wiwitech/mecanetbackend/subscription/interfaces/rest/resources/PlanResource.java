package com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Plan resource for API responses
 */
public record PlanResource(
        Long id,
        String name,
        String description,
        BigDecimal cost,
        Boolean isActive
) {} 