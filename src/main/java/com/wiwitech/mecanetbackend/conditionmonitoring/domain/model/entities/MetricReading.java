package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.Instant;

/**
 * Lectura histórica de una métrica.
 */
@Getter
@Entity
@Table(name = "metric_readings",
       indexes = @Index(name = "idx_machine_metric_time",
                        columnList = "machineId, metricId, measuredAt"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetricReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long machineId;
    private Long metricId;
    private Double value;
    private Instant measuredAt;
    private Long tenantId;

    public MetricReading(Long machineId, Long metricId,
                         Double value, Instant measuredAt, Long tenantId) {
        this.machineId  = machineId;
        this.metricId   = metricId;
        this.value      = value;
        this.measuredAt = measuredAt;
        this.tenantId   = tenantId;
    }
}