package com.wiwitech.mecanetbackend.iam.interfaces.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiwitech.mecanetbackend.iam.domain.model.commands.CreateUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.DeleteUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.UpdateUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetAllUsersQuery;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetUserByIdQuery;
import com.wiwitech.mecanetbackend.iam.domain.services.UserCommandService;
import com.wiwitech.mecanetbackend.iam.domain.services.UserQueryService;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.CreateUserResource;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.UpdateUserResource;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.UserResource;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.CreateUserCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.UpdateUserCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * UsersController
 * <p>
 *     This controller is responsible for handling user-related requests.
 *     It exposes endpoints for user management with multitenancy support.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Available User Management Endpoints")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    /**
     * Get all users for the current tenant
     * @return list of users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users for the current tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully.")})
    public ResponseEntity<List<UserResource>> getAllUsers() {
        logger.info("Getting all users for current tenant");
        
        var getAllUsersQuery = new GetAllUsersQuery();
        var users = userQueryService.handle(getAllUsersQuery);
        var userResources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} users", userResources.size());
        return ResponseEntity.ok(userResources);
    }

    /**
     * Get user by ID
     * @param userId the user ID
     * @return the user resource
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get user by ID for the current tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        logger.info("Getting user by ID: {}", userId);
        
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var userOptional = userQueryService.handle(getUserByIdQuery);
        
        if (userOptional.isEmpty()) {
            logger.warn("User not found with ID: {}", userId);
            throw new RuntimeException("User not found");
        }
        
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(userOptional.get());
        logger.info("User retrieved successfully: {}", userResource.username());
        return ResponseEntity.ok(userResource);
    }
    
    /**
     * Create a new user
     * @param createUserResource the user creation data
     * @return the created user resource
     */
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user in the current tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.")})
    public ResponseEntity<UserResource> createUser(@RequestBody CreateUserResource createUserResource) {
        logger.info("Creating new user: {}", createUserResource.username());
        
        CreateUserCommand command = CreateUserCommandFromResourceAssembler.toCommandFromResource(createUserResource);
        var user = userCommandService.handle(command);
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);
        
        logger.info("User created successfully: {}", userResource.username());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }
    
    /**
     * Update an existing user
     * @param userId the user ID
     * @param updateUserResource the user update data
     * @return the updated user resource
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update an existing user in the current tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "400", description = "Bad request.")})
    public ResponseEntity<UserResource> updateUser(@PathVariable Long userId, 
                                                   @RequestBody UpdateUserResource updateUserResource) {
        logger.info("Updating user with ID: {}", userId);
        
        UpdateUserCommand command = UpdateUserCommandFromResourceAssembler.toCommandFromResource(userId, updateUserResource);
        var user = userCommandService.handle(command);
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);
        
        logger.info("User updated successfully: {}", userResource.username());
        return ResponseEntity.ok(userResource);
    }
    
    /**
     * Delete (deactivate) a user
     * @param userId the user ID
     * @return the deactivated user resource
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete (deactivate) a user in the current tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.")})
    public ResponseEntity<UserResource> deleteUser(@PathVariable Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        
        DeleteUserCommand command = new DeleteUserCommand(userId);
        var user = userCommandService.handle(command);
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user);
        
        logger.info("User deleted successfully: {}", userResource.username());
        return ResponseEntity.ok(userResource);
    }
} 