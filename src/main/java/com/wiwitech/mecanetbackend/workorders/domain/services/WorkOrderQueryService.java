package com.wiwitech.mecanetbackend.workorders.domain.services;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Domain service that processes WorkOrder-related queries.
 */
public interface WorkOrderQueryService {
    Optional<WorkOrder> handle(GetWorkOrderByIdQuery query);
    List<WorkOrder> handle(GetWorkOrdersByStatusQuery query);
} 