package com.wiwitech.mecanetbackend.subscription.domain.model.commands;

public record CreateSubscriptionCommand(
    Long tenantId,
    Long planId
) {
    public CreateSubscriptionCommand {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID must be positive");
        }
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }
    }
} 