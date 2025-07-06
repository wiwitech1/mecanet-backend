package com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Resource for changing subscription plan
 */
public record ChangeSubscriptionPlanResource(
        @NotNull(message = "New plan ID cannot be null")
        @Positive(message = "New plan ID must be positive")
        Long newPlanId
) {} 