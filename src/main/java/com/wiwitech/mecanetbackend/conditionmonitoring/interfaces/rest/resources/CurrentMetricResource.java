package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources;

import java.time.Instant;

/** DTO de la última lectura de una métrica. */
public record CurrentMetricResource(
        Long metricId,
        String metricName,
        String unit,
        Double value,
        Instant measuredAt) {}