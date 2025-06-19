package com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.entities.MetricReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface MetricReadingRepository extends JpaRepository<MetricReading, Long> {
    Page<MetricReading> findByMachineIdAndMetricIdAndMeasuredAtBetween(
            Long machineId, Long metricId, Instant from, Instant to, Pageable pageable);
}