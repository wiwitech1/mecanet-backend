package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.MachineCreatedEvent;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MachineMetrics;
import com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories.MachineMetricsRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializes an empty MachineMetrics aggregate whenever a machine
 * is registered in the AssetManagement bounded-context.
 */
@Component
public class MachineCreatedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MachineCreatedEventHandler.class);
    private final MachineMetricsRepository repository;

    public MachineCreatedEventHandler(MachineMetricsRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void on(MachineCreatedEvent event) {
        try {
            TenantContext.setCurrentTenantId(event.tenantId());

            repository.findByMachineIdAndTenantIdValue(event.machineId(), event.tenantId())
                      .or(() -> {
                          LOG.info("Creating MachineMetrics aggregate for machine {}", event.machineId());
                          return java.util.Optional.of(
                                  repository.save(new MachineMetrics(
                                          event.machineId(),
                                          new TenantId(event.tenantId())
                                  )));
                      });

        } finally {
            TenantContext.clear();
        }
    }
}