package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.commandservices;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MetricDefinition;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands.SeedMetricsCommand;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MetricDefinitionCommandService;
import com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories.MetricDefinitionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MetricDefinitionCommandServiceImpl implements MetricDefinitionCommandService {

    private final MetricDefinitionRepository repository;
    private static final List<MetricDefinition> DEFAULT_METRICS = List.of(
        new MetricDefinition("Kilometraje", "km"),
        new MetricDefinition("Horas de uso", "h"),
        new MetricDefinition("Ciclos de trabajo", "ciclos"),
        new MetricDefinition("Horas de motor", "h"),
        new MetricDefinition("Temperatura", "°C"),
        new MetricDefinition("Presión", "bar"),
        new MetricDefinition("Vibración", "mm/s")
    );

    public MetricDefinitionCommandServiceImpl(MetricDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(SeedMetricsCommand command) {
        DEFAULT_METRICS.forEach(md -> {
            if (!repository.existsByName(md.getName())) {
                repository.save(md);
            }
        });
    }
}