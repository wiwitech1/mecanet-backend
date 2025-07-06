// UpdatePlantCommandFromResourceAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.UpdatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ContactInfo;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Location;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.UpdatePlantResource;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;

/**
 * Assembler to convert UpdatePlantResource to UpdatePlantCommand
 */
public class UpdatePlantCommandFromResourceAssembler {
    
    public static UpdatePlantCommand toCommandFromResource(Long plantId, UpdatePlantResource resource) {
        Location location = new Location(
            resource.address(),
            resource.city(),
            resource.country()
        );
        
        ContactInfo contactInfo = new ContactInfo(
            new PhoneNumber(resource.contactPhone()),
            new EmailAddress(resource.contactEmail())
        );
        
        return new UpdatePlantCommand(
            plantId,
            resource.name(),
            location,
            contactInfo
        );
    }
}