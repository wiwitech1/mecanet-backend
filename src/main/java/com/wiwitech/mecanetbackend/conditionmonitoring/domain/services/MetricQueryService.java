package com.wiwitech.mecanetbackend.conditionmonitoring.domain.services;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MetricDefinition;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries.GetAllMetricDefinitionsQuery;

import java.util.List;

public interface MetricQueryService {
    List<MetricDefinition> handle(GetAllMetricDefinitionsQuery query);
}
