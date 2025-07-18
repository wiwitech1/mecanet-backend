package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;

public record SetPendingExecutionCommand(
        WorkOrderId workOrderId,
        Long adminUserId
) {} 