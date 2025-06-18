// ProductionLineResourceFromEntityAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.ProductionLine;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.ProductionLineResource;

/**
 * Assembler to convert ProductionLine entity to ProductionLineResource
 */
public class ProductionLineResourceFromEntityAssembler {
    
    public static ProductionLineResource toResourceFromEntity(ProductionLine entity) {
        return new ProductionLineResource(
            entity.getId(),
            entity.getName(),
            entity.getCode(),
            entity.getCapacity().getUnitsPerHour(),
            "units",
            entity.getStatus().name(),
            entity.getPlantId()
        );
    }
}