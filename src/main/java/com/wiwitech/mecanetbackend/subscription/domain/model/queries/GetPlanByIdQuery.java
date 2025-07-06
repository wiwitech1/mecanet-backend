package com.wiwitech.mecanetbackend.subscription.domain.model.queries;

public record GetPlanByIdQuery(
    Long planId
) {
    public GetPlanByIdQuery {
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }
    }
} 