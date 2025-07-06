package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.events.MetricDefinitionCreatedEvent;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * Catálogo global de tipos de métrica.
 */
@Getter
@Entity
@Table(name = "metric_definitions",
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetricDefinition
        extends AuditableAbstractAggregateRoot<MetricDefinition> {

    @Column(nullable = false, length = 100)
    private String name;              // p. ej. "Kilometraje"

    @Column(nullable = false, length = 20)
    private String unit;              // p. ej. "km", "h", "°C"

    public MetricDefinition(String name, String unit) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Metric name is required");
        if (unit == null || unit.trim().isEmpty())
            throw new IllegalArgumentException("Metric unit is required");

        this.name = name;
        this.unit = unit;

        // Evento de dominio
        addDomainEvent(new MetricDefinitionCreatedEvent(getId(), name, unit));
    }
}