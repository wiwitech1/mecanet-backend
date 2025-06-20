package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ProductionLineId {

    @Column(name = "production_line_id")
    private Long value;

    public ProductionLineId(Long value) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("ProductionLineId inválido");
        this.value = value;
    }
}