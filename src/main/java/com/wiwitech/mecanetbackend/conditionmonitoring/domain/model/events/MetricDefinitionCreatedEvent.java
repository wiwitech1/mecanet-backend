package com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.events;

/**
 * Evento emitido al crear un nuevo tipo de métrica.
 */
public record MetricDefinitionCreatedEvent(
        Long metricId,
        String name,
        String unit
) {}