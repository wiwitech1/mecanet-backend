package com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateTechnicianCommand;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.TechnicianRequestResource;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;

public class CreateTechnicianCommandFromResourceAssembler {
    public static CreateTechnicianCommand toCommand(TechnicianRequestResource resource) {
        return new CreateTechnicianCommand(
            new UserId(resource.iamUserId),
            resource.username,
            resource.firstName,
            resource.lastName,
            new EmailAddress(resource.email)
        );
    }
} 