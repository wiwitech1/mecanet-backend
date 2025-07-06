package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources;

/** DTO de catálogo de métricas. */
public record MetricDefinitionResource(
        Long id,
        String name,
        String unit) {}