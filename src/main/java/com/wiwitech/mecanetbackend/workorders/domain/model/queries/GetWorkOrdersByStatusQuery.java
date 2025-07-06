package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;

/**
 * Query to list WorkOrders filtered by their current status.
 */
public record GetWorkOrdersByStatusQuery(WorkOrderStatus status) {
} 