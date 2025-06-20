package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Threshold {

    @Column(name = "threshold_value", nullable = false)
    private Double value;

    public Threshold(Double value) {
        if (value == null) throw new IllegalArgumentException("El umbral es obligatorio");
        this.value = value;
    }
}