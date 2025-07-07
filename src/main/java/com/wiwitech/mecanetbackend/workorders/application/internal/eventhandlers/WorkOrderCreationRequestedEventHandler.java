package com.wiwitech.mecanetbackend.workorders.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.WorkOrderCreationRequestedEvent;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateWorkOrderCommand;
import com.wiwitech.mecanetbackend.workorders.domain.services.WorkOrderCommandService;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("workOrdersCreationRequestedHandler")
@Slf4j
@RequiredArgsConstructor
public class WorkOrderCreationRequestedEventHandler {

    private final WorkOrderCommandService commandService;

    @EventListener
    public void on(WorkOrderCreationRequestedEvent e) {
        log.info("Received WorkOrderCreationRequestedEvent for origin plan {} task {}", e.originPlanId(), e.originTaskId());

        // Generar de manera sencilla un identificador único para la nueva orden de trabajo
        long generatedId = System.currentTimeMillis();

        try {
            TenantContext.setCurrentTenantId(e.tenantId());
            commandService.handle(new CreateWorkOrderCommand(
                    new WorkOrderId(generatedId),
                    new DynamicPlanId(e.originPlanId()),
                    new DynamicTaskId(e.originTaskId()),
                    new MachineId(e.machineId()),
                    e.title(),
                    e.description(),
                    Set.copyOf(e.requiredSkillIds()),
                    new com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId(e.tenantId())
            ));
        } finally {
            TenantContext.clear();
        }
    }
} 