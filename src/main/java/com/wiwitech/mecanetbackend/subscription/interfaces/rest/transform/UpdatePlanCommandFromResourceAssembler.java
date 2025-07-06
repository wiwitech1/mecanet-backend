package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.commands.UpdatePlanCommand;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.UpdatePlanResource;

/**
 * Assembler to convert UpdatePlanResource to UpdatePlanCommand
 */
public class UpdatePlanCommandFromResourceAssembler {
    
    public static UpdatePlanCommand toCommand(Long planId, UpdatePlanResource resource) {
        return new UpdatePlanCommand(
                planId,
                resource.description(),
                resource.cost()
        );
    }
} 