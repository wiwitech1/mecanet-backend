package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries;

public record GetCurrentMetricQuery(Long machineId, Long metricId) {
    public GetCurrentMetricQuery {
        if (machineId == null || machineId <= 0 ||
            metricId  == null || metricId  <= 0)
            throw new IllegalArgumentException("ids must be positive");
    }
}