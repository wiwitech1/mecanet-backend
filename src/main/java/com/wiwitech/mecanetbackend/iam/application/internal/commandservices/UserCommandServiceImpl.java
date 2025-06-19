package com.wiwitech.mecanetbackend.iam.application.internal.commandservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import com.wiwitech.mecanetbackend.iam.application.internal.outboundservices.hashing.HashingService;
import com.wiwitech.mecanetbackend.iam.application.internal.outboundservices.tokens.TokenService;
import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.Tenant;
import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.User;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.CreateUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.DeleteUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.SignUpCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.commands.UpdateUserCommand;
import com.wiwitech.mecanetbackend.iam.domain.model.entities.Role;
import com.wiwitech.mecanetbackend.iam.domain.model.valueobjects.Roles;
import com.wiwitech.mecanetbackend.iam.domain.services.UserCommandService;
import com.wiwitech.mecanetbackend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.wiwitech.mecanetbackend.iam.infrastructure.persistence.jpa.repositories.TenantRepository;
import com.wiwitech.mecanetbackend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.iam.domain.model.events.UserCreatedEvent;

/**
 * User command service implementation
 * This service handles user command operations with multitenancy support
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;
    
    public UserCommandServiceImpl(UserRepository userRepository, TenantRepository tenantRepository,
                                 RoleRepository roleRepository, HashingService hashingService, 
                                 TokenService tokenService,
                                 ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public ImmutablePair<User, String> handle(SignUpCommand command) {
        logger.info("Starting tenant registration process for RUC: {}", command.ruc());
        
        // Validate tenant doesn't exist
        if (tenantRepository.existsByRuc(command.ruc())) {
            throw new RuntimeException("A company with this RUC already exists");
        }
        
        if (tenantRepository.existsByEmail(command.tenantEmail())) {
            throw new RuntimeException("A company with this email already exists");
        }
        
        // Validate admin user doesn't exist
        if (userRepository.existsByUsername(command.username())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("User email already exists");
        }
        
        // Create tenant
        Tenant tenant = new Tenant(
            command.ruc(),
            command.legalName(),
            command.commercialName(),
            command.address(),
            command.city(),
            command.country(),
            command.tenantPhone(),
            command.tenantEmail(),
            command.website()
        );
        
        Tenant savedTenant = tenantRepository.save(tenant);
        logger.info("Tenant created successfully with ID: {}", savedTenant.getId());
        
        // Set tenant context for user creation
        TenantContext.setCurrentTenantId(savedTenant.getId());
        
        try {
            // Get admin role
            Role adminRole = roleRepository.findByName(Roles.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            // Create admin user
            String hashedPassword = hashingService.encode(command.password());
            
            User adminUser = new User(
                command.username(),
                hashedPassword,
                command.email(),
                command.firstName(),
                command.lastName(),
                savedTenant.getId(),
                List.of(adminRole)
            );
            
            User savedUser = userRepository.save(adminUser);
            logger.info("Admin user created successfully with ID: {}", savedUser.getId());
            
            // Generate token
            String token = tokenService.generateToken(savedUser.getUsername(), savedTenant.getId());
            logger.info("Token generated successfully for user: {}", savedUser.getUsername());
            
            // ---------- Publicar evento de dominio ----------
            eventPublisher.publishEvent(
                new UserCreatedEvent(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getEmail(),
                    savedTenant.getId(),
                    savedUser.getRoles()
                             .stream()
                             .map(Role::getStringName)
                             .toList()
                )
            );
            // ------------------------------------------------

            return ImmutablePair.of(savedUser, token);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Override
    public User handle(CreateUserCommand command) {
        logger.info("Creating new user: {}", command.username());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Check if user already exists in this tenant
        if (userRepository.existsByUsernameAndTenantId(command.username(), tenantId)) {
            throw new RuntimeException("Username already exists in this organization");
        }
        
        if (userRepository.existsByEmailAndTenantId(command.email(), tenantId)) {
            throw new RuntimeException("Email already exists in this organization");
        }
        
        // Get roles
        List<Role> roles = getRolesFromNames(command.roles());
        
        // Create user
        String hashedPassword = hashingService.encode(command.password());
        
        User user = new User(
            command.username(),
            hashedPassword,
            command.email(),
            command.firstName(),
            command.lastName(),
            tenantId,
            roles
        );
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        
        // ---------- Publicar evento de dominio ----------
        eventPublisher.publishEvent(
            new UserCreatedEvent(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                tenantId,
                savedUser.getRoles()
                         .stream()
                         .map(Role::getStringName)
                         .toList()
            )
        );
        // ------------------------------------------------

        return savedUser;
    }
    
    @Override
    public User handle(UpdateUserCommand command) {
        logger.info("Updating user with ID: {}", command.userId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Find user in current tenant
        User user = userRepository.findByIdAndTenantId(command.userId(), tenantId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if email is already used by another user
        if (!user.getEmail().equals(command.email()) && 
            userRepository.existsByEmailAndTenantId(command.email(), tenantId)) {
            throw new RuntimeException("Email already exists in this organization");
        }
        
        // Update user fields
        user.setEmail(command.email().value());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        
        // Update roles if provided
        if (command.roles() != null && !command.roles().isEmpty()) {
            List<Role> roles = getRolesFromNames(command.roles());
            user.getRoles().clear();
            user.addRoles(roles);
        }
        
        User savedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    @Override
    public User handle(DeleteUserCommand command) {
        logger.info("Deactivating user with ID: {}", command.userId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Find user in current tenant
        User user = userRepository.findByIdAndTenantId(command.userId(), tenantId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Deactivate user
        user.deactivate();
        
        User savedUser = userRepository.save(user);
        logger.info("User deactivated successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    private List<Role> getRolesFromNames(List<String> roleNames) {
        List<Role> roles = new ArrayList<>();
        
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                try {
                    Roles roleEnum = Roles.valueOf(roleName);
                    Role role = roleRepository.findByName(roleEnum)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + roleName);
                }
            }
        } else {
            // Default role
            Role defaultRole = roleRepository.findByName(Roles.ROLE_TECHNICAL)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        }
        
        return roles;
    }
} 