package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * VO que encapsula el valor y la fecha de la lectura vigente.
 */
@Embeddable
public record CurrentMetric(
        @Column(nullable = false)        Double value,
        @Column(name = "measured_at",
                 nullable = false)        Instant measuredAt) {

    public CurrentMetric {
        if (value == null)
            throw new IllegalArgumentException("Metric value cannot be null");
        if (measuredAt == null)
            throw new IllegalArgumentException("measuredAt cannot be null");
    }
}