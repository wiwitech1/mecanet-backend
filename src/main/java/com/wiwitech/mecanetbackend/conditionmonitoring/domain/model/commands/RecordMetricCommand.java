package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands;

import java.time.Instant;

/**
 * Comando para registrar una lectura de métrica.
 */
public record RecordMetricCommand(
        Long machineId,
        Long metricId,
        Double value,
        Instant measuredAt        // opcional; la capa de aplicación usará Instant.now() si es null
) {
    public RecordMetricCommand {
        if (machineId == null || machineId <= 0)
            throw new IllegalArgumentException("machineId must be positive");
        if (metricId == null || metricId <= 0)
            throw new IllegalArgumentException("metricId must be positive");
        if (value == null)
            throw new IllegalArgumentException("value is required");
    }
}