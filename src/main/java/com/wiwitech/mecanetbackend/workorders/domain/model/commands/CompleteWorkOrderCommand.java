package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import java.time.LocalDateTime;

public record CompleteWorkOrderCommand(
        WorkOrderId workOrderId,
        TechnicianId technicianId,
        LocalDateTime endAt,
        String conclusions
) {} 