package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.workorders.domain.model.events.WorkOrderStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles WorkOrderStartedEvent domain events.
 * This handler processes internal domain events when a work order execution starts.
 */
@Component
public class WorkOrderStartedEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkOrderStartedEventHandler.class);
    
    @EventListener
    public void on(WorkOrderStartedEvent event) {
        logger.info("Work order started: {} at {} for tenant: {}", 
            event.workOrderId().getValue(),
            event.startAt(),
            event.tenantId());
        
        // Here you can add business logic like:
        // - Update technician availability status
        // - Start time tracking
        // - Notify supervisors about work progress
        // - Update real-time dashboards
        // - Trigger material consumption processes
        // - Start monitoring and alerting
    }
} 