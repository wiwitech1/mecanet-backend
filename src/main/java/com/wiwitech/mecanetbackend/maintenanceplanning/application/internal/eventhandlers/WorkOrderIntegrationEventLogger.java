package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.WorkOrderCreationRequestedEvent;

/**
 * Handler temporal que registra en log los eventos de integración.
 * Cuando el BC WorkOrders esté completo, este componente puede eliminarse
 * y reemplazarse por un verdadero consumer que cree órdenes de trabajo.
 */
@Component
public class WorkOrderIntegrationEventLogger {

    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderIntegrationEventLogger.class);

    @EventListener
    public void on(WorkOrderCreationRequestedEvent event) {
        LOG.info("=== WORK ORDER CREATION REQUESTED ===");
        LOG.info("Type: {}", event.workOrderType());
        LOG.info("Title: {}", event.title());
        LOG.info("Description: {}", event.description());
        LOG.info("Machine ID: {}", event.machineId());
        LOG.info("Required Skills: {}", event.requiredSkillIds());
        LOG.info("Tenant: {}", event.tenantId());
        LOG.info("Origin Plan: {}", event.originPlanId());
        LOG.info("Origin Task: {}", event.originTaskId());
        LOG.info("=====================================");
        
        // TODO: Cuando WorkOrders esté listo, aquí se llamaría:
        // workOrderCommandService.handle(new CreateWorkOrderCommand(...));
    }
}
