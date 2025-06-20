package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.events.MetricRecordedEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.MachineId;
import com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories.MaintenancePlanRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MetricRecordedEventHandler {

    private final MaintenancePlanRepository repository;

    public MetricRecordedEventHandler(MaintenancePlanRepository repository) {
        this.repository = repository;
    }

    @EventListener
    @Transactional
    public void on(MetricRecordedEvent event) {
        Long tenant = TenantContext.getCurrentTenantId();        // usa tenant actual (o default)
        repository.findByMetricIdValueAndTenantIdValue(event.metricId(), tenant)
                  .forEach(plan -> {
                      plan.evaluateTrigger(new MachineId(event.machineId()), event.value());
                      repository.save(plan);                     // persiste cambios y eventos
                  });
    }
}