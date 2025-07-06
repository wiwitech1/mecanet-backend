package com.wiwitech.mecanetbackend.iam.domain.services;

import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.User;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.SignInCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetAllUsersQuery;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetUserByIdQuery;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetUserByUsernameQuery;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Optional;

/**
 * User query service interface
 * This interface defines the contract for user query operations
 */
public interface UserQueryService {
    
    /**
     * Handle get all users query
     * @param query the get all users query
     * @return list of all users
     */
    List<User> handle(GetAllUsersQuery query);
    
    /**
     * Handle get user by ID query
     * @param query the get user by ID query
     * @return optional user if found
     */
    Optional<User> handle(GetUserByIdQuery query);
    
    /**
     * Handle get user by username query
     * @param query the get user by username query
     * @return optional user if found
     */
    Optional<User> handle(GetUserByUsernameQuery query);
    
    /**
     * Handle sign in command (authentication)
     * @param command the sign in command
     * @return pair containing the authenticated user and generated token
     */
    ImmutablePair<User, String> handle(SignInCommand command);
} 