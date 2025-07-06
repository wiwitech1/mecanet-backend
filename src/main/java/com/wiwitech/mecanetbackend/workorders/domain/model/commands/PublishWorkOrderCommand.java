package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;

public record PublishWorkOrderCommand(
        WorkOrderId workOrderId,
        Long adminUserId
) {} 