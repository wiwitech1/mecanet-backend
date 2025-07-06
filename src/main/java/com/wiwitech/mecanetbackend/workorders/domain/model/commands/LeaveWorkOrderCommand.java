package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;

public record LeaveWorkOrderCommand(
        WorkOrderId workOrderId,
        TechnicianId technicianId,
        String reason
) {} 