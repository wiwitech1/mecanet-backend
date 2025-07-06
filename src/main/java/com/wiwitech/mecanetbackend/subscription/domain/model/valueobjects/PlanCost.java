package com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PlanCost {

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal value;

    public PlanCost(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Plan cost cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Plan cost cannot be negative");
        }
        this.value = value;
    }

    public PlanCost(double value) {
        this(BigDecimal.valueOf(value));
    }

    public boolean isFree() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
} 