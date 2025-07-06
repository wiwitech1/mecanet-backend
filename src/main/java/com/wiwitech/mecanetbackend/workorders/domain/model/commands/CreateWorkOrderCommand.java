package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import java.util.Set;

public record CreateWorkOrderCommand(
        WorkOrderId workOrderId,
        DynamicPlanId planId,
        DynamicTaskId taskId,
        MachineId machineId,
        String title,
        String description,
        Set<Long> requiredSkillIds,
        TenantId tenantId
) {} 