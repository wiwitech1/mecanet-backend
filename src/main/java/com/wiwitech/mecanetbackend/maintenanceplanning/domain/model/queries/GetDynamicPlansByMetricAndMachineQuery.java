package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries;

public record GetDynamicPlansByMetricAndMachineQuery(Long metricDefinitionId, Long machineId) {}