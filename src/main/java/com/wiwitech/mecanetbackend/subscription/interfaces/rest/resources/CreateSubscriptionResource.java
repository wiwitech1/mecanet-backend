package com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for creating a new subscription
 */
public record CreateSubscriptionResource(
        @NotNull(message = "Plan ID cannot be null")
        @Positive(message = "Plan ID must be positive")
        Long planId
) {} 