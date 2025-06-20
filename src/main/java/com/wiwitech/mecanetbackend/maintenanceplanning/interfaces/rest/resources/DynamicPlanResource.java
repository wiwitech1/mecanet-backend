package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanStatus;

import java.time.LocalDate;
import java.util.Set;

public record DynamicPlanResource(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        PlanStatus status,
        Long metricDefinitionId,
        Double threshold,
        Set<DynamicTaskResource> tasks
) {}
