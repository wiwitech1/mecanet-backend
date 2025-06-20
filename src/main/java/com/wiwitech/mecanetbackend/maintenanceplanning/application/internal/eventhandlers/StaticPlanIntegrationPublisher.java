package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.StaticPlanDayGeneratedEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories.MaintenancePlanRepository;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticPlanItem;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticTask;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.WorkOrderCreationRequestedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Publica eventos de integración cuando se ejecuta un día de plan estático
 */
@Component
public class StaticPlanIntegrationPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(StaticPlanIntegrationPublisher.class);
    
    private final MaintenancePlanRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public StaticPlanIntegrationPublisher(MaintenancePlanRepository repository,
                                        ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(StaticPlanDayGeneratedEvent event) {
        LOG.info("Processing static plan day generated: planId={}, dayIndex={}, cycleStart={}", 
                event.planId(), event.dayIndex(), event.cycleStart());

        // Obtener el plan y el ítem del día
        repository.findById(event.planId())
                  .filter(p -> p instanceof StaticMaintenancePlan)
                  .map(p -> (StaticMaintenancePlan) p)
                  .ifPresent(plan -> {
                      StaticPlanItem item = plan.getItems()
                                              .stream()
                                              .filter(i -> i.getDayIndex() == event.dayIndex())
                                              .findFirst()
                                              .orElse(null);
                      
                      if (item != null && !item.getTasks().isEmpty()) {
                          // Crear una orden de trabajo por cada tarea del ítem
                          item.getTasks().forEach(task -> {
                              WorkOrderCreationRequestedEvent integrationEvent = new WorkOrderCreationRequestedEvent(
                                      "STATIC_MAINTENANCE",              // tipo de orden
                                      buildTaskTitle(plan, task, event.dayIndex()), // título descriptivo
                                      task.getDescription(),             // descripción
                                      task.getMachineId().getValue(),    // máquina objetivo
                                      extractSkillIds(task),             // skills requeridas
                                      plan.getTenantId().getValue(),     // tenant
                                      event.planId(),                    // referencia al plan origen
                                      task.getId()                       // referencia a la tarea origen
                              );
                              
                              eventPublisher.publishEvent(integrationEvent);
                              LOG.info("Published WorkOrderCreationRequestedEvent for static task: {} (day {})", 
                                      task.getId(), event.dayIndex());
                          });
                      }
                  });
    }

    private String buildTaskTitle(StaticMaintenancePlan plan, StaticTask task, int dayIndex) {
        return String.format("[%s - Día %d] %s", plan.getName(), dayIndex, task.getName());
    }

    private Set<Long> extractSkillIds(StaticTask task) {
        return task.getRequiredSkills()
                   .stream()
                   .map(skillId -> skillId.getValue())
                   .collect(Collectors.toSet());
    }
}