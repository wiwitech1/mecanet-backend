package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.ChangeSubscriptionPlanResource;

/**
 * Assembler to convert ChangeSubscriptionPlanResource to ChangeSubscriptionPlanCommand
 */
public class ChangeSubscriptionPlanCommandFromResourceAssembler {
    
    public static ChangeSubscriptionPlanCommand toCommand(Long tenantId, ChangeSubscriptionPlanResource resource) {
        return new ChangeSubscriptionPlanCommand(
                tenantId,
                resource.newPlanId()
        );
    }
} 