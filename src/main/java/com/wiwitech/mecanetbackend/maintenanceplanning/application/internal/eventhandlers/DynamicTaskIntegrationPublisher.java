package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.DynamicTaskTriggeredEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories.MaintenancePlanRepository;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.DynamicTask;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.WorkOrderCreationRequestedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Publica eventos de integración cuando se dispara una tarea dinámica
 */
@Component
public class DynamicTaskIntegrationPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicTaskIntegrationPublisher.class);
    
    private final MaintenancePlanRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public DynamicTaskIntegrationPublisher(MaintenancePlanRepository repository,
                                         ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(DynamicTaskTriggeredEvent event) {
        LOG.info("Processing dynamic task triggered: planId={}, taskId={}, machineId={}", 
                event.planId(), event.taskId(), event.machineId());

        // Obtener detalles de la tarea desde el repositorio
        repository.findById(event.planId())
                  .filter(p -> p instanceof DynamicMaintenancePlan)
                  .map(p -> (DynamicMaintenancePlan) p)
                  .ifPresent(plan -> {
                      DynamicTask task = plan.getTasks()
                                           .stream()
                                           .filter(t -> t.getId().equals(event.taskId()))
                                           .findFirst()
                                           .orElse(null);
                      
                      if (task != null) {
                          // Crear evento de integración con toda la información necesaria
                          WorkOrderCreationRequestedEvent integrationEvent = new WorkOrderCreationRequestedEvent(
                                  "DYNAMIC_MAINTENANCE",           // tipo de orden
                                  task.getName(),                  // título de la OT
                                  task.getDescription(),           // descripción
                                  event.machineId(),               // máquina objetivo
                                  extractSkillIds(task),           // skills requeridas
                                  plan.getTenantId().getValue(),   // tenant
                                  event.planId(),                  // referencia al plan origen
                                  event.taskId()                   // referencia a la tarea origen
                          );
                          
                          eventPublisher.publishEvent(integrationEvent);
                          LOG.info("Published WorkOrderCreationRequestedEvent for task: {}", event.taskId());
                      }
                  });
    }

    private Set<Long> extractSkillIds(DynamicTask task) {
        return task.getRequiredSkills()
                   .stream()
                   .map(skillId -> skillId.getValue())
                   .collect(Collectors.toSet());
    }
}