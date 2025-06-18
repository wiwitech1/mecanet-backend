package com.wiwitech.mecanetbackend.iam.interfaces.rest.resources;

/**
 * Sign up resource
 * This resource is used for tenant registration with admin user creation
 */
public record SignUpResource(
    // Tenant data
    String ruc,
    String legalName,
    String commercialName,
    String address,
    String city,
    String country,
    String tenantPhone,
    String tenantEmail,
    String website,
    
    // Admin user data
    String username,
    String password,
    String email,
    String firstName,
    String lastName
) {} 