package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class MetricDefinitionId {

    @Column(name = "metric_definition_id")
    private Long value;

    public MetricDefinitionId(Long value) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("MetricDefinitionId inválido");
        this.value = value;
    }
}