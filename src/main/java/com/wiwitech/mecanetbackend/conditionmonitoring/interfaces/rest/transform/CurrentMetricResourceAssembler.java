package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.valueobjects.CurrentMetric;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources.CurrentMetricResource;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MetricDefinition;

public class CurrentMetricResourceAssembler {
    public static CurrentMetricResource toResource(Long metricId,
                                                   CurrentMetric cm,
                                                   MetricDefinition def) {
        return new CurrentMetricResource(metricId,
                                         def.getName(),
                                         def.getUnit(),
                                         cm.value(),
                                         cm.measuredAt());
    }
}