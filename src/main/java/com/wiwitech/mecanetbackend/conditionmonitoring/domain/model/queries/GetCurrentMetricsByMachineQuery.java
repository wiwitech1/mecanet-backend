
package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries;
public record GetCurrentMetricsByMachineQuery(Long machineId) {
    public GetCurrentMetricsByMachineQuery {
        if (machineId == null || machineId <= 0)
            throw new IllegalArgumentException("machineId must be positive");
    }
}