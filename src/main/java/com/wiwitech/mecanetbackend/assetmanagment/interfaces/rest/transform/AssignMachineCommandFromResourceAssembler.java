// AssignMachineCommandFromResourceAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.AssignMachineToProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.AssignMachineResource;

/**
 * Assembler to convert AssignMachineResource to AssignMachineToProductionLineCommand
 */
public class AssignMachineCommandFromResourceAssembler {
    
    public static AssignMachineToProductionLineCommand toCommandFromResource(Long machineId, AssignMachineResource resource) {
        return new AssignMachineToProductionLineCommand(
            machineId,
            resource.productionLineId()
        );
    }
}