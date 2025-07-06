package com.wiwitech.mecanetbackend.iam.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.iam.domain.model.commands.SignUpCommand;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.SignUpResource;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;

/**
 * Assembler to convert SignUpResource to SignUpCommand
 */
public class SignUpCommandFromResourceAssembler {
    
    /**
     * Transform from SignUpResource to SignUpCommand
     * @param resource the SignUpResource
     * @return the SignUpCommand
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
            resource.ruc(),
            resource.legalName(),
            resource.commercialName(),
            resource.address(),
            resource.city(),
            resource.country(),
            resource.tenantPhone() != null ? new PhoneNumber(resource.tenantPhone()) : null,
            new EmailAddress(resource.tenantEmail()),
            resource.website(),
            resource.username(),
            resource.password(),
            new EmailAddress(resource.email()),
            resource.firstName(),
            resource.lastName()
        );
    }
} 