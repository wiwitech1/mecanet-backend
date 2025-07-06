package com.wiwitech.mecanetbackend.subscription.domain.model.events;

public record SubscriptionCreatedEvent(
    Long tenantId,
    Long planId
) {} 