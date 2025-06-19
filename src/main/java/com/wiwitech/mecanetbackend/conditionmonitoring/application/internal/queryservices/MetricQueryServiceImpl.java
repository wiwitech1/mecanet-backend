package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.queryservices;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MetricDefinition;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries.GetAllMetricDefinitionsQuery;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MetricQueryService;
import com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories.MetricDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricQueryServiceImpl implements MetricQueryService {

    private final MetricDefinitionRepository repo;
    public MetricQueryServiceImpl(MetricDefinitionRepository repo) { this.repo = repo; }

    @Override
    public List<MetricDefinition> handle(GetAllMetricDefinitionsQuery query) {
        return repo.findAll();
    }
}