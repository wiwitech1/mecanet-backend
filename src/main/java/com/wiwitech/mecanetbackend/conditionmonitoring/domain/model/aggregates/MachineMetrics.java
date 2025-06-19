package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.entities.MetricReading;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.events.MetricRecordedEvent;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.valueobjects.CurrentMetric;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.Instant;
import java.util.*;

/**
 * Aggregate Root que mantiene las lecturas de una máquina.
 */
@Getter
@Entity
@Table(name = "machine_metrics",
       uniqueConstraints = @UniqueConstraint(columnNames = "machine_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MachineMetrics
        extends AuditableAbstractAggregateRoot<MachineMetrics> {

    @Column(name = "machine_id", nullable = false)
    private Long machineId;

    @Embedded
    @AttributeOverride(name = "value",
        column = @Column(name = "tenant_id", nullable = false))
    private TenantId tenantId;

    /** Valor vigente de cada métrica (clave = metricId). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "machine_metric_values",
            joinColumns = @JoinColumn(name = "machine_metrics_id"))
    @MapKeyColumn(name = "metric_id")
    private Map<Long, CurrentMetric> currentReadings = new HashMap<>();

    /** Histórico de lecturas */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_metrics_id")
    private List<MetricReading> readings = new ArrayList<>();

    public MachineMetrics(Long machineId, TenantId tenantId) {
        this.machineId = machineId;
        this.tenantId  = tenantId;
    }

    /**
     * Registra una nueva lectura, actualiza el valor vigente
     * y emite un evento de dominio.
     */
    public void record(Long metricId, Double value, Instant measuredAt) {
        if (metricId == null || value == null)
            throw new IllegalArgumentException("metricId and value are required");
        Instant ts = measuredAt != null ? measuredAt : Instant.now();

        // Histórico
        readings.add(new MetricReading(machineId, metricId, value, ts, tenantId.getValue()));
        // Valor vigente
        currentReadings.put(metricId, new CurrentMetric(value, ts));

        addDomainEvent(new MetricRecordedEvent(machineId, metricId, value, ts));
    }
}