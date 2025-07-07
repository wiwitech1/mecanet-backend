package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.workorders.domain.model.events.WorkOrderPublishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles WorkOrderPublishedEvent domain events.
 * This handler processes internal domain events when a work order is published.
 */
@Component
public class WorkOrderPublishedEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkOrderPublishedEventHandler.class);
    
    @EventListener
    public void on(WorkOrderPublishedEvent event) {
        logger.info("Work order published: {} for tenant: {}", 
            event.workOrderId().getValue(),
            event.tenantId());
        
        // Here you can add business logic like:
        // - Notify available technicians
        // - Update work order boards
        // - Send notifications to supervisors
        // - Update metrics and dashboards
        // - Trigger material reservation processes
    }
} 