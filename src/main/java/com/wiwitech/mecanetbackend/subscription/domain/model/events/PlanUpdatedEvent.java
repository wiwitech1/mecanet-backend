package com.wiwitech.mecanetbackend.subscription.domain.model.events;

public record PlanUpdatedEvent(
    Long planId,
    String planName
) {} 