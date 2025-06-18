// CreateProductionLineCommandFromResourceAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreateProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Capacity;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.CreateProductionLineResource;

/**
 * Assembler to convert CreateProductionLineResource to CreateProductionLineCommand
 */
public class CreateProductionLineCommandFromResourceAssembler {
    
    public static CreateProductionLineCommand toCommandFromResource(CreateProductionLineResource resource) {
        // Capacity necesita unitsPerHour y maxUnitsPerDay
        // Calculamos maxUnitsPerDay asumiendo 24 horas de operación máxima
        Integer maxUnitsPerDay = resource.maxUnitsPerHour() * 24;
        
        Capacity capacity = new Capacity(
            resource.maxUnitsPerHour(),
            maxUnitsPerDay
        );
        
        return new CreateProductionLineCommand(
            resource.name(),
            resource.code(),
            capacity,
            resource.plantId()
        );
    }
}