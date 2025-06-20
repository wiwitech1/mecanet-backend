package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;


import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanStatus;

import java.time.LocalDate;
import java.util.Set;

public record StaticPlanResource(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        PlanStatus status,
        Long productionLineId,
        Integer cyclePeriodInDays,
        Integer durationInDays,
        Set<StaticPlanItemResource> items
) {}