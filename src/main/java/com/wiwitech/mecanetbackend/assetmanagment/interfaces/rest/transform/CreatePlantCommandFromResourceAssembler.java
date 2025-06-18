// CreatePlantCommandFromResourceAssembler.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ContactInfo;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.Location;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.CreatePlantResource;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;

/**
 * Assembler to convert CreatePlantResource to CreatePlantCommand
 */
public class CreatePlantCommandFromResourceAssembler {
    
    public static CreatePlantCommand toCommandFromResource(CreatePlantResource resource) {
        Location location = new Location(
            resource.address(),
            resource.city(),
            resource.country()
        );
        
        ContactInfo contactInfo = new ContactInfo(
            new PhoneNumber(resource.contactPhone()),
            new EmailAddress(resource.contactEmail())
        );
        
        return new CreatePlantCommand(
            resource.name(),
            location,
            contactInfo
        );
    }
}