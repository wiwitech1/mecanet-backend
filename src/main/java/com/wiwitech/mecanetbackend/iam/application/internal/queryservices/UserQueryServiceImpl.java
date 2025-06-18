package com.wiwitech.mecanetbackend.iam.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import com.wiwitech.mecanetbackend.iam.application.internal.outboundservices.hashing.HashingService;
import com.wiwitech.mecanetbackend.iam.application.internal.outboundservices.tokens.TokenService;
import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.User;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.SignInCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetAllUsersQuery;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetUserByIdQuery;
import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetUserByUsernameQuery;
import com.wiwitech.mecanetbackend.iam.domain.services.UserQueryService;
import com.wiwitech.mecanetbackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * User query service implementation
 * This service handles user query operations with multitenancy support
 */
@Service
@Slf4j
public class UserQueryServiceImpl implements UserQueryService {
    
    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    
    public UserQueryServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }
    
    @Override
    public List<User> handle(GetAllUsersQuery query) {
        // Filter by current tenant
        Long tenantId = TenantContext.getCurrentTenantId();
        log.info("Getting all users for tenant {}", tenantId);
        return userRepository.findAllByTenantId(tenantId);
    }
    
    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        // Filter by current tenant
        Long tenantId = TenantContext.getCurrentTenantId();
        return userRepository.findByIdAndTenantId(query.userId(), tenantId);
    }
    
    @Override
    public Optional<User> handle(GetUserByUsernameQuery query) {
        // Filter by current tenant
        Long tenantId = TenantContext.getCurrentTenantId();
        return userRepository.findByUsernameAndTenantId(query.username(), tenantId);
    }
    
    @Override
    public ImmutablePair<User, String> handle(SignInCommand command) {
        // Find user by username and current tenant
        User user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        
        // Verify password
        if (!hashingService.matches(command.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw new RuntimeException("User account is disabled");
        }
        
        // Generate token
        String token = tokenService.generateToken(user.getUsername(), user.getTenantId());
        
        return ImmutablePair.of(user, token);
    }
} 