package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.entities.PlanAttribute;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.PlanAttributeResource;

/**
 * Assembler to convert PlanAttribute entity to PlanAttributeResource
 */
public class PlanAttributeResourceFromEntityAssembler {
    
    public static PlanAttributeResource toResource(PlanAttribute attribute) {
        return new PlanAttributeResource(
                attribute.getId(),
                attribute.getAttributeNameValue(),
                attribute.getAttributeValueValue(),
                attribute.isUnlimited()
        );
    }
} 