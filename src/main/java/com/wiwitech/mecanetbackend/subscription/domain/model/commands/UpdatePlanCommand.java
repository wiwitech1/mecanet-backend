package com.wiwitech.mecanetbackend.subscription.domain.model.commands;

import java.math.BigDecimal;

public record UpdatePlanCommand(
    Long planId,
    String description,
    BigDecimal cost
) {
    public UpdatePlanCommand {
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }
        if (description == null) {
            throw new IllegalArgumentException("Plan description cannot be null");
        }
        if (cost == null) {
            throw new IllegalArgumentException("Plan cost cannot be null");
        }
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Plan cost cannot be negative");
        }
    }
} 