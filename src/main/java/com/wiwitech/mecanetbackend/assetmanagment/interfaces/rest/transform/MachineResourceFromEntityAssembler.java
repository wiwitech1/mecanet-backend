// MachineResourceFromEntityAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Machine;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.MachineResource;

/**
 * Assembler to convert Machine entity to MachineResource
 */
public class MachineResourceFromEntityAssembler {
    
    public static MachineResource toResourceFromEntity(Machine entity) {
        return new MachineResource(
            entity.getId(),
            entity.getSerialNumber(),
            entity.getName(),
            entity.getSpecs().getManufacturer(),
            entity.getSpecs().getModel(),
            entity.getSpecs().getType(),
            entity.getSpecs().getPowerConsumption(),
            entity.getStatus().name(),
            entity.getProductionLineId(),
            entity.getMaintenanceInfo().getLastMaintenanceDate(),
            entity.getMaintenanceInfo().getNextMaintenanceDate()
        );
    }
}