package com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.MaintenancePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long> {

    Optional<MaintenancePlan> findByIdAndTenantIdValue(Long id, Long tenantId);

    /* dinámico */
    List<DynamicMaintenancePlan> findByMetricIdValueAndTenantIdValue(Long metricId, Long tenantId);

    /* estático: todos los planes activos cuyo periodo incluya la fecha */
    List<StaticMaintenancePlan> findByPeriodStartDateLessThanEqualAndPeriodEndDateGreaterThanEqualAndTenantIdValue(
            LocalDate start, LocalDate end, Long tenantId);

    /* listar por tenant */
    List<DynamicMaintenancePlan> findByTenantIdValue(Long tenantId);
    List<StaticMaintenancePlan>  findStaticMaintenancePlanByTenantIdValue(Long tenantId);
}