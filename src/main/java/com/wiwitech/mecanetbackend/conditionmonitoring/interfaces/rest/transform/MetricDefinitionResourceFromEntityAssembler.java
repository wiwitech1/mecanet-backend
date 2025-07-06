package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MetricDefinition;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources.MetricDefinitionResource;

/** Converts MetricDefinition entities to REST resources. */
public class MetricDefinitionResourceFromEntityAssembler {
    public static MetricDefinitionResource toResource(MetricDefinition entity) {
        return new MetricDefinitionResource(entity.getId(), entity.getName(), entity.getUnit());
    }
}