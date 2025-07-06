package com.wiwitech.mecanetbackend.subscription.domain.model.events;

public record SubscriptionLimitExceededEvent(
    Long tenantId,
    String resourceType,
    Integer limit,
    Integer currentUsage,
    String attemptedAction
) {} 