package com.wiwitech.mecanetbackend.conditionmonitoring.domain.services;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries.*;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

public interface MachineMetricsQueryService {

    Map<Long, ?> handle(GetCurrentMetricsByMachineQuery query);      // metricId -> CurrentMetric
    Optional<?> handle(GetCurrentMetricQuery query);                 // CurrentMetric o vacío
    Page<?>      handle(GetMetricReadingsQuery query);               // página de MetricReading
}