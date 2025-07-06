package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;

public record JoinWorkOrderCommand(
        WorkOrderId workOrderId,
        TechnicianId technicianId
) {} 