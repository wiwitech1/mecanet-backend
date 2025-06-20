package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.CreateDynamicPlanCommand;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.CreateDynamicPlanResource;

public class CreateDynamicPlanCommandFromResourceAssembler {
    
    public static CreateDynamicPlanCommand toCommand(CreateDynamicPlanResource resource) {
        return new CreateDynamicPlanCommand(
                resource.name(),
                resource.startDate(),
                resource.endDate(),
                resource.metricDefinitionId(),
                resource.threshold()
        );
    }
}