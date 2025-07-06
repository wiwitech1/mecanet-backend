package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.StaticPlanResource;

import java.util.stream.Collectors;

public class StaticPlanResourceFromEntityAssembler {
    
    public static StaticPlanResource toResource(StaticMaintenancePlan plan) {
        return new StaticPlanResource(
                plan.getId(),
                plan.getName(),
                plan.getPeriod().getStartDate(),
                plan.getPeriod().getEndDate(),
                plan.getStatus(),
                plan.getLineId().getValue(),
                plan.getCyclePeriodInDays(),
                plan.getDurationInDays(),
                plan.getItems().stream()
                    .map(StaticPlanItemResourceFromEntityAssembler::toResource)
                    .collect(Collectors.toSet())
        );
    }
}