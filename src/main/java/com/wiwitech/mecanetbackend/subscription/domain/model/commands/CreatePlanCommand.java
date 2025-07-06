package com.wiwitech.mecanetbackend.subscription.domain.model.commands;

import java.math.BigDecimal;

public record CreatePlanCommand(
    String name,
    String description,
    BigDecimal cost
) {
    public CreatePlanCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan name cannot be null or empty");
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