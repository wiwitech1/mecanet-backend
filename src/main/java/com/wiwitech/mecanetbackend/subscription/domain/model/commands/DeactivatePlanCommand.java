package com.wiwitech.mecanetbackend.subscription.domain.model.commands;

public record DeactivatePlanCommand(
    Long planId
) {
    public DeactivatePlanCommand {
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }
    }
} 