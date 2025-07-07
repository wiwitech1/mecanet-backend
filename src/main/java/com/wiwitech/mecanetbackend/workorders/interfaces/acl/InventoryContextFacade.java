package com.wiwitech.mecanetbackend.workorders.interfaces.acl;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.UpdateMaterialsCommand;
import java.util.Set;
import java.util.Map;

/**
 * ACL port that allows WorkOrders BC to interact with Inventory BC without
 * depending on its internal API or schema.
 */
public interface InventoryContextFacade {

    /** Reserve the requested materials when order is still being prepared */
    void reserveMaterials(WorkOrderId workOrderId, Set<UpdateMaterialsCommand.MaterialLine> lines);

    /** Consume previously reserved stock when execution starts */
    void consumeReservations(WorkOrderId workOrderId);

    /** Finalize the consumption with real quantities at completion */
    void finalizeConsumptions(WorkOrderId workOrderId, Map<Long, Integer> finalQtyPerItem);
} 