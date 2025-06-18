package com.wiwitech.mecanetbackend.iam.domain.model.commands;

import java.util.List;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;

/**
 * Create user command
 * This command is used by admin users to create new users within their tenant
 */
public record CreateUserCommand(
    String username,
    String password,
    EmailAddress email,
    String firstName,
    String lastName,
    List<String> roles
) {
    public CreateUserCommand {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
    }
} 