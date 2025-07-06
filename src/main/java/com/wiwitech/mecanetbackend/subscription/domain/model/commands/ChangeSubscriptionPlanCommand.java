package com.wiwitech.mecanetbackend.subscription.domain.model.commands;

public record ChangeSubscriptionPlanCommand(
    Long tenantId,
    Long newPlanId
) {
    public ChangeSubscriptionPlanCommand {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID must be positive");
        }
        if (newPlanId == null || newPlanId <= 0) {
            throw new IllegalArgumentException("New plan ID must be positive");
        }
    }
} 