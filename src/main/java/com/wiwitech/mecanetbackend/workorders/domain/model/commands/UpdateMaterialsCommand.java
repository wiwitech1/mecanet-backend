package com.wiwitech.mecanetbackend.workorders.domain.model.commands;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import java.util.Set;

public record UpdateMaterialsCommand(
        WorkOrderId workOrderId,
        Set<MaterialLine> materials
) {
    public record MaterialLine(Long itemId, String sku, String name, int quantity) {}
} 