package com.wiwitech.mecanetbackend.iam.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.iam.domain.model.entities.Role;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}