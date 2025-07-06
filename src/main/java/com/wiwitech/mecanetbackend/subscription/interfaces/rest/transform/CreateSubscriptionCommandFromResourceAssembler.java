package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.CreateSubscriptionResource;

/**
 * Assembler to convert CreateSubscriptionResource to CreateSubscriptionCommand
 */
public class CreateSubscriptionCommandFromResourceAssembler {
    
    public static CreateSubscriptionCommand toCommand(Long tenantId, CreateSubscriptionResource resource) {
        return new CreateSubscriptionCommand(
                tenantId,
                resource.planId()
        );
    }
} 