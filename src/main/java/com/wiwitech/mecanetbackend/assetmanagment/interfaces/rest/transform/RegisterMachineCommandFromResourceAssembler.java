// RegisterMachineCommandFromResourceAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.RegisterMachineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MachineSpecs;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.RegisterMachineResource;

/**
 * Assembler to convert RegisterMachineResource to RegisterMachineCommand
 */
public class RegisterMachineCommandFromResourceAssembler {
    
    public static RegisterMachineCommand toCommandFromResource(RegisterMachineResource resource) {
        MachineSpecs specs = new MachineSpecs(
            resource.manufacturer(),
            resource.model(),
            resource.type(),
            resource.powerConsumption()
        );
        
        return new RegisterMachineCommand(
            resource.serialNumber(),
            resource.name(),
            specs
        );
    }
}