package com.wiwitech.mecanetbackend.workorders.interfaces.acl;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import java.util.Map;

/**
 * ACL port that allows WorkOrders BC to interact with Inventory BC without
 * depending on its internal API or schema.
 * 
 * Simplified version - only deducts stock when work order completes.
 */
public interface InventoryContextFacade {

    /** 
     * Deduct the actual consumed materials from inventory stock when work order completes.
     * @param workOrderId the work order that is completing
     * @param finalQtyPerItem map of inventory item ID to final consumed quantity
     */
    void deductStock(WorkOrderId workOrderId, Map<Long, Integer> finalQtyPerItem);
} 