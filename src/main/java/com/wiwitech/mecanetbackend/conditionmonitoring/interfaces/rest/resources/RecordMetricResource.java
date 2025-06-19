package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources;

import java.time.Instant;

public record RecordMetricResource(
        Long metricId,
        Double value,
        Instant measuredAt) {}