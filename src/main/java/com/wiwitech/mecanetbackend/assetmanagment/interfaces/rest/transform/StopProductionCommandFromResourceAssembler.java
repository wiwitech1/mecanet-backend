// StopProductionCommandFromResourceAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StopProductionCommand;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.StopProductionResource;

/**
 * Assembler to convert StopProductionResource to StopProductionCommand
 */
public class StopProductionCommandFromResourceAssembler {
    
    public static StopProductionCommand toCommandFromResource(Long productionLineId, StopProductionResource resource) {
        return new StopProductionCommand(
            productionLineId,
            resource.reason()
        );
    }
}