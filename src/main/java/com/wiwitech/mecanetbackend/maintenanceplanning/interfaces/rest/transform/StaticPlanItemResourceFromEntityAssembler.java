package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticPlanItem;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.StaticPlanItemResource;

import java.util.stream.Collectors;

public class StaticPlanItemResourceFromEntityAssembler {
    
    public static StaticPlanItemResource toResource(StaticPlanItem item) {
        return new StaticPlanItemResource(
                item.getId(),
                item.getDayIndex(),
                item.getTasks().stream()
                    .map(StaticTaskResourceFromEntityAssembler::toResource)
                    .collect(Collectors.toSet())
        );
    }
}