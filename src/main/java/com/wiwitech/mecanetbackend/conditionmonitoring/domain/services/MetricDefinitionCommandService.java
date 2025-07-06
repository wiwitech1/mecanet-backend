package com.wiwitech.mecanetbackend.conditionmonitoring.domain.services;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands.SeedMetricsCommand;
public interface MetricDefinitionCommandService {
    void handle(SeedMetricsCommand command);
}