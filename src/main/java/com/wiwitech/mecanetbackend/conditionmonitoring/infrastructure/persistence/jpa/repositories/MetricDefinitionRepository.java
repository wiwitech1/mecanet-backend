package com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MetricDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MetricDefinitionRepository extends JpaRepository<MetricDefinition, Long> {
    boolean existsByName(String name);
}