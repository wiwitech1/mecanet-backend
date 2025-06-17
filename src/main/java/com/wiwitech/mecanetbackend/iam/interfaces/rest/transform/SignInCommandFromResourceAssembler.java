package com.wiwitech.mecanetbackend.iam.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.iam.domain.model.commands.SignInCommand;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(signInResource.username(), signInResource.password());
    }
}