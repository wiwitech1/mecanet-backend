package com.wiwitech.mecanetbackend.subscription.domain.model.events;

public record PlanDeactivatedEvent(
    Long planId,
    String planName
) {} 