package com.wiwitech.mecanetbackend.subscription.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.iam.domain.model.events.UserCreatedEvent;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionCommandService;
import com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * UserCreatedEventHandler class
 * Handles UserCreatedEvent to automatically create a FREE subscription for new tenants
 */
@Component("subscriptionUserCreatedEventHandler")
public class UserCreatedEventHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserCreatedEventHandler.class);
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    
    private final SubscriptionCommandService subscriptionCommandService;
    private final PlanRepository planRepository;

    public UserCreatedEventHandler(SubscriptionCommandService subscriptionCommandService, 
                                  PlanRepository planRepository) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.planRepository = planRepository;
    }

    /**
     * Handle UserCreatedEvent to create automatic FREE subscription for new admin users
     * Only admin users trigger tenant creation, so only they need automatic subscription
     */
    @EventListener
    public void on(UserCreatedEvent event) {
        LOG.info("Received UserCreatedEvent for user: {} with roles: {}", 
                event.username(), event.roles());
        
        // Only create subscription for admin users (they create new tenants)
        if (!event.roles().contains(ADMIN_ROLE)) {
            LOG.debug("Skipping subscription creation for non-admin user: {}", event.username());
            return;
        }
        
        // Set tenant context for the operation
        TenantContext.setCurrentTenantId(event.tenantId());
        
        try {
            // Find FREE plan
            var freePlan = planRepository.findByNameValueAndIsActiveTrue("FREE");
            if (freePlan.isEmpty()) {
                LOG.error("FREE plan not found! Cannot create automatic subscription for tenant: {}", 
                         event.tenantId());
                return;
            }
            
            // Create subscription command
            var createSubscriptionCommand = new CreateSubscriptionCommand(
                event.tenantId(),
                freePlan.get().getId()
            );
            
            // Create subscription
            var subscription = subscriptionCommandService.handle(createSubscriptionCommand);
            
            LOG.info("Automatic FREE subscription created successfully for tenant: {} (user: {})", 
                    event.tenantId(), event.username());
            
        } catch (Exception e) {
            LOG.error("Failed to create automatic subscription for tenant: {} (user: {}): {}", 
                     event.tenantId(), event.username(), e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }
}