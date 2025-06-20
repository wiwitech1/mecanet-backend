package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.AddTaskToDynamicPlanCommand;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.AddTaskToDynamicPlanResource;

import java.util.Collections;

public class AddTaskToDynamicPlanCommandFromResourceAssembler {
    
    public static AddTaskToDynamicPlanCommand toCommand(Long planId, AddTaskToDynamicPlanResource resource) {
        return new AddTaskToDynamicPlanCommand(
                planId,
                resource.machineId(),
                resource.taskName(),
                resource.description(),
                resource.skillIds() != null ? resource.skillIds() : Collections.emptySet()
        );
    }
}