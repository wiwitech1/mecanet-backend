package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.queryservices;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.services.MaintenancePlanQueryService;
import com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories.MaintenancePlanRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenancePlanQueryServiceImpl implements MaintenancePlanQueryService {

    private final MaintenancePlanRepository repository;

    public MaintenancePlanQueryServiceImpl(MaintenancePlanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MaintenancePlan> handle(GetMaintenancePlanByIdQuery query) {
        return repository.findByIdAndTenantIdValue(query.planId(), TenantContext.getCurrentTenantId());
    }

    @Override
    public List<DynamicMaintenancePlan> handle(GetDynamicPlansByMetricAndMachineQuery query) {
        Long tenant = TenantContext.getCurrentTenantId();
        return repository.findByMetricIdValueAndTenantIdValue(query.metricDefinitionId(), tenant)
                         .stream()
                         .filter(p -> p.getTasks()
                                       .stream()
                                       .anyMatch(t -> t.getMachineId().getValue().equals(query.machineId())))
                         .toList();
    }

    @Override
    public List<StaticMaintenancePlan> handle(GetStaticPlansScheduledForDateQuery query) {
        Long tenant = TenantContext.getCurrentTenantId();
        LocalDate date = query.date();

        return repository.findByPeriodStartDateLessThanEqualAndPeriodEndDateGreaterThanEqualAndTenantIdValue(
                             date, date, tenant)
                         .stream()
                         .filter(p -> isScheduledFor(p, date))
                         .toList();
    }

    @Override
    public List<DynamicMaintenancePlan> handle(GetAllDynamicPlansQuery query) {
        return repository.findByTenantIdValue(TenantContext.getCurrentTenantId());
    }

    @Override
    public List<StaticMaintenancePlan> handle(GetAllStaticPlansQuery query) {
        return repository.findStaticMaintenancePlanByTenantIdValue(TenantContext.getCurrentTenantId());
    }

    /* -------- helper -------- */
    private boolean isScheduledFor(StaticMaintenancePlan plan, LocalDate date) {
        if (!plan.isActiveOn(date)) return false;

        long daysSinceStart = ChronoUnit.DAYS.between(plan.getPeriod().getStartDate(), date);
        int  dayOfCycle     = (int) (daysSinceStart % plan.getCyclePeriodInDays()) + 1;
        return dayOfCycle <= plan.getDurationInDays();
    }
}