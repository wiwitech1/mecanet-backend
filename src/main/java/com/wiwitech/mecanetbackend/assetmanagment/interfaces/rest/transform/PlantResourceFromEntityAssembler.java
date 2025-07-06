// PlantResourceFromEntityAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Plant;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.PlantResource;

/**
 * Assembler to convert Plant entity to PlantResource
 */
public class PlantResourceFromEntityAssembler {
    
    public static PlantResource toResourceFromEntity(Plant entity) {
        return new PlantResource(
            entity.getId(),
            entity.getName(),
            entity.getLocation().getAddress(),
            entity.getLocation().getCity(),
            entity.getLocation().getCountry(),
            entity.getContactInfo().getPhone().value(),
            entity.getContactInfo().getEmail().value(),
            entity.isActive()
        );
    }
}