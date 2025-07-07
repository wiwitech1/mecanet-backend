package com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateWorkOrderCommand;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.WorkOrderRequestResource;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;

public class CreateWorkOrderCommandFromResourceAssembler {
    public static CreateWorkOrderCommand toCommand(WorkOrderRequestResource resource) {
        return new CreateWorkOrderCommand(
            null, // El ID se genera en el handler normalmente
            new DynamicPlanId(resource.planId),
            new DynamicTaskId(resource.taskId),
            new MachineId(resource.machineId),
            resource.title,
            resource.description,
            resource.requiredSkillIds,
            new TenantId(resource.tenantId)
        );
    }
} 