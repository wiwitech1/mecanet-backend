package com.wiwitech.mecanetbackend.workorders.domain.model.events;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.DynamicPlanId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.DynamicTaskId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.MachineId;

public record WorkOrderCreatedEvent(
        WorkOrderId workOrderId,
        DynamicPlanId planId,
        DynamicTaskId taskId,
        MachineId machineId,
        Long tenantId
) {} 