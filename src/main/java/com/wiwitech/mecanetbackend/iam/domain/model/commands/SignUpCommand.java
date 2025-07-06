package com.wiwitech.mecanetbackend.iam.domain.model.commands;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;

/**
 * Sign up command
 * This command is used to register a new tenant (company) with an admin user
 */
public record SignUpCommand(
    // Tenant data
    String ruc,
    String legalName,
    String commercialName,
    String address,
    String city,
    String country,
    PhoneNumber tenantPhone,
    EmailAddress tenantEmail,
    String website,
    
    // Admin user data
    String username,
    String password,
    EmailAddress email,
    String firstName,
    String lastName
) {
    public SignUpCommand {
        // Tenant validations
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new IllegalArgumentException("RUC cannot be null or empty");
        }
        if (legalName == null || legalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Legal name cannot be null or empty");
        }
        if (tenantEmail == null) {
            throw new IllegalArgumentException("Tenant email cannot be null");
        }
        
        // Admin user validations
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
    }
} 