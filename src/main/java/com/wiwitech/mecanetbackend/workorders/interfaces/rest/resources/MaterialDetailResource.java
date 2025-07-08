package com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources;

/**
 * Resource representing detailed material information including requested and final quantities.
 */
public class MaterialDetailResource {
    public Long itemId;
    public String itemSku;
    public String itemName;
    public Integer requestedQty;
    public Integer finalQty;
    
    public MaterialDetailResource() {}
    
    public MaterialDetailResource(Long itemId, String itemSku, String itemName, Integer requestedQty, Integer finalQty) {
        this.itemId = itemId;
        this.itemSku = itemSku;
        this.itemName = itemName;
        this.requestedQty = requestedQty;
        this.finalQty = finalQty;
    }
} 