package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries;

import java.time.Instant;

public record GetMetricReadingsQuery(
        Long machineId,
        Long metricId,
        Instant from,
        Instant to,
        int page,
        int size) {

    public GetMetricReadingsQuery {
        if (machineId == null || machineId <= 0 ||
            metricId  == null || metricId  <= 0)
            throw new IllegalArgumentException("ids must be positive");
        if (page < 0 || size <= 0)
            throw new IllegalArgumentException("invalid paging");
    }
}