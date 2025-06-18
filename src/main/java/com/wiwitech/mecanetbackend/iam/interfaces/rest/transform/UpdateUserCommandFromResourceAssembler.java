package com.wiwitech.mecanetbackend.iam.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.iam.domain.model.commands.UpdateUserCommand;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.UpdateUserResource;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;

/**
 * Assembler to convert UpdateUserResource to UpdateUserCommand
 */
public class UpdateUserCommandFromResourceAssembler {
    
    public static UpdateUserCommand toCommandFromResource(Long userId, UpdateUserResource resource) {
        return new UpdateUserCommand(
            userId,
            new EmailAddress(resource.email()),
            resource.firstName(),
            resource.lastName(),
            resource.roles()
        );
    }
} 