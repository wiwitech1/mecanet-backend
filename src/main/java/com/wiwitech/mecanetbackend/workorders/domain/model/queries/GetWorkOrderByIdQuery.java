package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;

/**
 * Query to obtain a WorkOrder by its identifier.
 */
public record GetWorkOrderByIdQuery(WorkOrderId workOrderId) {
} 