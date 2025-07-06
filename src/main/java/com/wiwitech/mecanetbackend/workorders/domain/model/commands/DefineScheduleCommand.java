package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.Schedule;

public record DefineScheduleCommand(
        WorkOrderId workOrderId,
        Schedule schedule,
        int maxTechnicians
) {} 