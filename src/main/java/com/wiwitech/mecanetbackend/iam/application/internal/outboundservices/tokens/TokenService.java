package com.wiwitech.mecanetbackend.iam.application.internal.outboundservices.tokens;

import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;

/**
 * TokenService interface
 * This interface is used to generate and validate tokens
 */
public interface TokenService {

    /**
     * Generate a token for a given authentication
     * @param authentication the authentication object
     * @return String the token
     */
    String generateToken(Authentication authentication);

    /**
     * Generate a token for a given username
     * @param username the username
     * @return String the token
     */
    String generateToken(String username);

    /**
     * Generate a token for a given username and tenant ID
     * @param username the username
     * @param tenantId the tenant ID
     * @return String the token
     */
    String generateToken(String username, Long tenantId);

    /**
     * Extract the username from a token
     * @param token the token
     * @return String the username
     */
    String getUsernameFromToken(String token);

    /**
     * Extract the tenant ID from a token
     * @param token the token
     * @return Long the tenant ID
     */
    Long getTenantIdFromToken(String token);

    /**
     * Validate a token
     * @param token the token
     * @return boolean true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extract the bearer token from an HTTP request
     * @param request the HTTP request
     * @return String the bearer token, or null if not found
     */
    String getBearerTokenFrom(HttpServletRequest request);
}