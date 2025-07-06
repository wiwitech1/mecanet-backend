package com.wiwitech.mecanetbackend.iam.interfaces.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiwitech.mecanetbackend.iam.domain.services.UserCommandService;
import com.wiwitech.mecanetbackend.iam.domain.services.UserQueryService;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.SignInResource;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.SignUpResource;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * AuthenticationController
 * <p>
 *     This controller is responsible for handling authentication requests.
 *     It exposes two endpoints:
 *     <ul>
 *         <li>POST /api/v1/authentication/sign-in</li>
 *         <li>POST /api/v1/authentication/sign-up</li>
 *     </ul>
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Available Authentication Endpoints")
public class AuthenticationController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public AuthenticationController(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    /**
     * Handles the sign-in request.
     * @param signInResource the sign-in request body.
     * @return the authenticated user resource.
     */
    @PostMapping("/sign-in")
    @Operation(summary = "Sign-in", description = "Sign-in with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid credentials.")})
    public ResponseEntity<?> signIn(@RequestBody SignInResource signInResource) {
        logger.info("Processing sign-in request for username: {}", signInResource.username());
        
        try {
            var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
            var authenticatedUser = userQueryService.handle(signInCommand);
            var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler
                    .toResourceFromEntity(authenticatedUser.getLeft(), authenticatedUser.getRight());
            
            logger.info("Sign-in successful for username: {}", signInResource.username());
            return ResponseEntity.ok(authenticatedUserResource);
        } catch (Exception e) {
            logger.error("Error during sign-in: {}", e.getMessage(), e);
            
            // ✅ Devolver JSON estructurado para el error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", 400);
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("path", "/api/v1/authentication/sign-in");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Handles the sign-up request.
     * @param signUpResource the sign-up request body.
     * @return the created user resource.
     */
    @PostMapping("/sign-up")
    @Operation(summary = "Sign-up", description = "Sign-up with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.")})
    public ResponseEntity<?> signUp(@RequestBody SignUpResource signUpResource) {
        logger.info("Processing sign-up request for username: {}", signUpResource.username());
        
        try {
            var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
            logger.info("SignUpCommand created successfully");
            
            var user = userCommandService.handle(signUpCommand);
            var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.getLeft());
            
            logger.info("User created successfully: {}", userResource.username());
            return new ResponseEntity<>(userResource, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error during sign-up: {}", e.getMessage(), e);
            
            // ✅ Devolver JSON estructurado para el error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", 400);
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("path", "/api/v1/authentication/sign-up");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    
} 