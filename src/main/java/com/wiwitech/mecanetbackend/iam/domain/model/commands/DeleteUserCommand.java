package com.wiwitech.mecanetbackend.iam.domain.model.commands;

/**
 * Delete user command
 * This command is used to delete (deactivate) an existing user
 */
public record DeleteUserCommand(Long userId) {
    public DeleteUserCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
} 