package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.PlanResource;

/**
 * Assembler to convert Plan entity to PlanResource
 */
public class PlanResourceFromEntityAssembler {
    
    public static PlanResource toResource(Plan plan) {
        return new PlanResource(
                plan.getId(),
                plan.getNameValue(),
                plan.getDescription(),
                plan.getCostValue(),
                plan.isActive()
        );
    }
} 