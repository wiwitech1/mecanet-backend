package com.wiwitech.mecanetbackend.workorders.domain.model.events;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;

public record WorkOrderPublishedEvent(
        WorkOrderId workOrderId,
        Long tenantId
) {} 