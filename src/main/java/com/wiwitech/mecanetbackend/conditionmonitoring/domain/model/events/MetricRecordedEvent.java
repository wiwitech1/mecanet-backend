package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.events;

import java.time.Instant;

/**
 * Evento emitido cada vez que se registra una lectura.
 */
public record MetricRecordedEvent(
        Long machineId,
        Long metricId,
        Double value,
        Instant measuredAt
) {}