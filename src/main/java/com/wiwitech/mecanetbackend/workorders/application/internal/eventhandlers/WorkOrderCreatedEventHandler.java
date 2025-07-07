package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.workorders.domain.model.events.WorkOrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles WorkOrderCreatedEvent domain events.
 * This handler processes internal domain events when a work order is created.
 */
@Component
public class WorkOrderCreatedEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkOrderCreatedEventHandler.class);
    
    @EventListener
    public void on(WorkOrderCreatedEvent event) {
        logger.info("Work order created: {} for plan: {}, task: {}, machine: {}, tenant: {}", 
            event.workOrderId().getValue(),
            event.planId().getValue(),
            event.taskId().getValue(),
            event.machineId().getValue(),
            event.tenantId());
        
        // Here you can add business logic like:
        // - Notify supervisors
        // - Update dashboards
        // - Trigger notifications
        // - Update metrics
    }
} 