package com.wiwitech.mecanetbackend.workorders.domain.model.events;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import java.time.LocalDateTime;

public record WorkOrderCompletedEvent(
        WorkOrderId workOrderId,
        LocalDateTime endAt,
        Long tenantId
) {} 