package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.commands.CreatePlanCommand;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.CreatePlanResource;

/**
 * Assembler to convert CreatePlanResource to CreatePlanCommand
 */
public class CreatePlanCommandFromResourceAssembler {
    
    public static CreatePlanCommand toCommand(CreatePlanResource resource) {
        return new CreatePlanCommand(
                resource.name(),
                resource.description(),
                resource.cost()
        );
    }
} 