package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources;

import java.time.Instant;

/** DTO de histórico. */
public record MetricReadingResource(
        Long readingId,
        Double value,
        Instant measuredAt) {}