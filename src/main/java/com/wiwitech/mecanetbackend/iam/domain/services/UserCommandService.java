package com.wiwitech.mecanetbackend.iam.domain.services;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.User;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.SignUpCommand;

import com.wiwitech.mecanetbackend.iam.domain.model.commands.CreateUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.DeleteUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.UpdateUserCommand;

/**
 * User command service interface
 * This service handles user command operations
 */
public interface UserCommandService {
    
    /**
     * Handle sign up command (creates tenant + admin user)
     * @param command the sign up command
     * @return pair of created user and JWT token
     */
    ImmutablePair<User, String> handle(SignUpCommand command);
    
    /**
     * Handle create user command (admin creates new user)
     * @param command the create user command
     * @return the created user
     */
    User handle(CreateUserCommand command);
    
    /**
     * Handle update user command
     * @param command the update user command
     * @return the updated user
     */
    User handle(UpdateUserCommand command);
    
    /**
     * Handle delete user command (deactivate user)
     * @param command the delete user command
     * @return the deactivated user
     */
    User handle(DeleteUserCommand command);
} 