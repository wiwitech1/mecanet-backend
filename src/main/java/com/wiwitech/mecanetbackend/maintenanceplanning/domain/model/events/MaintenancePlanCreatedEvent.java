package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanType;

public record MaintenancePlanCreatedEvent(Long planId, Long tenantId, PlanType planType) {}