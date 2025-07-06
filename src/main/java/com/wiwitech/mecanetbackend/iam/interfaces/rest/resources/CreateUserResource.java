package com.wiwitech.mecanetbackend.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Create user resource
 * This resource is used by admin users to create new users
 */
public record CreateUserResource(
    String username,
    String password,
    String email,
    String firstName,
    String lastName,
    List<String> roles
) {} 