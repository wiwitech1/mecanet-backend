package com.wiwitech.mecanetbackend.workorders.domain.model.events;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import java.time.LocalDateTime;

public record WorkOrderStartedEvent(
        WorkOrderId workOrderId,
        LocalDateTime startAt,
        Long tenantId
) {} 