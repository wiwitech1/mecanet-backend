package com.wiwitech.mecanetbackend.iam.domain.model.commands;

import java.util.List;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;

/**
 * Update user command
 * This command is used to update an existing user
 */
public record UpdateUserCommand(
    Long userId,
    EmailAddress email,
    String firstName,
    String lastName,
    List<String> roles
) {
    public UpdateUserCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
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