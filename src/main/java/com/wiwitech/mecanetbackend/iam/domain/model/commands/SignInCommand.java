package com.wiwitech.mecanetbackend.iam.domain.model.commands;

public record SignInCommand(String username, String password) {
    public SignInCommand {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }
}