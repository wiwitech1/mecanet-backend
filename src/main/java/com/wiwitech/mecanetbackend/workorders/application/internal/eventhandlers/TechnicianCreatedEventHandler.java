package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.workorders.domain.model.events.TechnicianCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles TechnicianCreatedEvent domain events.
 * This handler processes internal domain events when a technician is created.
 */
@Component
public class TechnicianCreatedEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(TechnicianCreatedEventHandler.class);
    
    @EventListener
    public void on(TechnicianCreatedEvent event) {
        logger.info("Technician created: {} with IAM user: {} for tenant: {}", 
            event.technicianId(),
            event.iamUserId().getValue(),
            event.tenantId());
        
        // Here you can add business logic like:
        // - Send welcome notifications
        // - Assign default skills
        // - Create technician profile
        // - Update technician availability boards
        // - Trigger onboarding processes
    }
} 