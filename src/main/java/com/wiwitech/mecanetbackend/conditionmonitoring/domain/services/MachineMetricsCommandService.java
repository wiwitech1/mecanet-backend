package com.wiwitech.mecanetbackend.conditionmonitoring.domain.services;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MachineMetrics;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands.RecordMetricCommand;

/**
 * Puerto de dominio para registrar lecturas de métricas.
 * Su implementación vivirá en la capa application.
 */
public interface MachineMetricsCommandService {
    MachineMetrics handle(RecordMetricCommand command);
}