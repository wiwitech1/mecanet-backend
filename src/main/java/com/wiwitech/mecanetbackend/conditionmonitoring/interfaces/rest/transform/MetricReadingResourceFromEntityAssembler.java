package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.entities.MetricReading;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources.MetricReadingResource;

public class MetricReadingResourceFromEntityAssembler {
    public static MetricReadingResource toResource(MetricReading r) {
        return new MetricReadingResource(r.getId(), r.getValue(), r.getMeasuredAt());
    }
}