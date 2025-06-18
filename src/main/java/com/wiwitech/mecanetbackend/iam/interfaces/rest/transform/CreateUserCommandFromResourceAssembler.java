package com.wiwitech.mecanetbackend.iam.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.iam.domain.model.commands.CreateUserCommand;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.CreateUserResource;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;

/**
 * Assembler to convert CreateUserResource to CreateUserCommand
 */
public class CreateUserCommandFromResourceAssembler {
    
    public static CreateUserCommand toCommandFromResource(CreateUserResource resource) {
        return new CreateUserCommand(
            resource.username(),
            resource.password(),
            new EmailAddress(resource.email()),
            resource.firstName(),
            resource.lastName(),
            resource.roles()
        );
    }
} 