package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.workorders.domain.model.events.WorkOrderCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles WorkOrderCompletedEvent domain events.
 * This handler processes internal domain events when a work order is completed.
 */
@Component
public class WorkOrderCompletedEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkOrderCompletedEventHandler.class);
    
    @EventListener
    public void on(WorkOrderCompletedEvent event) {
        logger.info("Work order completed: {} at {} for tenant: {}", 
            event.workOrderId().getValue(),
            event.endAt(),
            event.tenantId());
        
        // Here you can add business logic like:
        // - Update technician availability status
        // - Finalize material consumption
        // - Generate completion reports
        // - Update metrics and KPIs
        // - Notify stakeholders
        // - Trigger follow-up processes
        // - Update maintenance history
    }
} 