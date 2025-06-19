package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands.RecordMetricCommand;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources.RecordMetricResource;

public class RecordMetricCommandFromResourceAssembler {
    public static RecordMetricCommand toCommand(Long machineId, RecordMetricResource r) {
        return new RecordMetricCommand(machineId,
                                       r.metricId(),
                                       r.value(),
                                       r.measuredAt());
    }
}