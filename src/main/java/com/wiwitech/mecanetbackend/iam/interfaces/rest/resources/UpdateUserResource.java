package com.wiwitech.mecanetbackend.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Update user resource
 * This resource is used to update existing users
 */
public record UpdateUserResource(
    String email,
    String firstName,
    String lastName,
    List<String> roles
) {} 