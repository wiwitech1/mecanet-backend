package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events;

import java.util.Set;

/**
 * Evento de integración que solicita la creación de una orden de trabajo.
 * Puede ser consumido por el BC WorkOrders u otros sistemas interesados.
 */
public record WorkOrderCreationRequestedEvent(
        String workOrderType,        // "DYNAMIC_MAINTENANCE" | "STATIC_MAINTENANCE"
        String title,                // título de la orden
        String description,          // descripción detallada
        Long machineId,              // máquina objetivo
        Set<Long> requiredSkillIds,  // skills necesarias
        Long tenantId,               // tenant
        Long originPlanId,           // plan que originó la orden
        Long originTaskId            // tarea específica que la originó
) {}