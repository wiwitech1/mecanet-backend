package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import java.util.Map;

/**
 * Command to update final quantities of materials during work order execution.
 * This allows technicians to specify the actual quantities used vs the originally requested quantities.
 */
public record UpdateFinalQuantitiesCommand(
        WorkOrderId workOrderId,
        Map<Long, Integer> finalQuantities // itemId -> finalQty
) {
} 