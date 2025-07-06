package com.wiwitech.mecanetbackend.subscription.domain.model.events;

public record PlanCreatedEvent(
    Long planId,
    String planName
) {} 