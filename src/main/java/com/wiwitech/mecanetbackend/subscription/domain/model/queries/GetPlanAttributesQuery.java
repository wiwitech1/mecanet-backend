package com.wiwitech.mecanetbackend.subscription.domain.model.queries;

public record GetPlanAttributesQuery(
    Long planId
) {
    public GetPlanAttributesQuery {
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }
    }
} 