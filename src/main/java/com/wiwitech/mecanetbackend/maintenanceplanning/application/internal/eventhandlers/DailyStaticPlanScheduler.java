package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories.MaintenancePlanRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyStaticPlanScheduler {

    private final MaintenancePlanRepository repository;

    public DailyStaticPlanScheduler(MaintenancePlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la verificación cada día a las 02:00 (configurable vía propiedad maintenanceplanning.scheduler.staticPlans).
     */
    @Scheduled(cron = "${maintenanceplanning.scheduler.staticPlans:0 0 2 * * *}")
    @Transactional
    public void run() {
        LocalDate today = LocalDate.now();
        Long tenant     = TenantContext.getCurrentTenantId();

        repository.findByPeriodStartDateLessThanEqualAndPeriodEndDateGreaterThanEqualAndTenantIdValue(
                        today, today, tenant)
                  .forEach(plan -> {
                      plan.generateWorkOrdersFor(today);
                      repository.save(plan);
                  });
    }
}