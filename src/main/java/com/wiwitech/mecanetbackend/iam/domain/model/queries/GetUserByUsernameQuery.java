package com.wiwitech.mecanetbackend.iam.domain.model.queries;

/**
 * Get user by username query
 * <p>
 *     This class represents the query to get a user by its username.
 * </p>
 * @param username the username of the user
 */
public record GetUserByUsernameQuery(String username) {
}