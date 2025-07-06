package com.wiwitech.mecanetbackend.subscription.domain.model.queries;

public record GetSubscriptionByTenantQuery(
    Long tenantId
) {
    public GetSubscriptionByTenantQuery {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("Tenant ID must be positive");
        }
    }
} 