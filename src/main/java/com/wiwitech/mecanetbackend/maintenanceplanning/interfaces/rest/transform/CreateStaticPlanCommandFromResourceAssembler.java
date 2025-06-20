package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.CreateStaticPlanCommand;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.CreateStaticPlanResource;

public class CreateStaticPlanCommandFromResourceAssembler {
    
    public static CreateStaticPlanCommand toCommand(CreateStaticPlanResource resource) {
        return new CreateStaticPlanCommand(
                resource.name(),
                resource.startDate(),
                resource.endDate(),
                resource.productionLineId(),
                resource.cyclePeriodInDays(),
                resource.durationInDays()
        );
    }
}