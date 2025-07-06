package com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MachineMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MachineMetricsRepository extends JpaRepository<MachineMetrics, Long> {
    Optional<MachineMetrics> findByMachineIdAndTenantIdValue(Long machineId, Long tenantId);
}