package com.wiwitech.mecanetbackend.subscription.domain.model.events;

public record SubscriptionPlanChangedEvent(
    Long tenantId,
    Long oldPlanId,
    Long newPlanId
) {} 