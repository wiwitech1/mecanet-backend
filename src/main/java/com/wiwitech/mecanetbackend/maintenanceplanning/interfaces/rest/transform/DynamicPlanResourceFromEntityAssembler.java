package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.DynamicPlanResource;

import java.util.stream.Collectors;

public class DynamicPlanResourceFromEntityAssembler {
    
    public static DynamicPlanResource toResource(DynamicMaintenancePlan plan) {
        return new DynamicPlanResource(
                plan.getId(),
                plan.getName(),
                plan.getPeriod().getStartDate(),
                plan.getPeriod().getEndDate(),
                plan.getStatus(),
                plan.getMetricId().getValue(),
                plan.getThreshold().getValue(),
                plan.getTasks().stream()
                    .map(DynamicTaskResourceFromEntityAssembler::toResource)
                    .collect(Collectors.toSet())
        );
    }
}