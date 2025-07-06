package com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PlanName {

    @Column(name = "name", length = 50, unique = true)
    private String value;

    public PlanName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan name cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Plan name cannot exceed 50 characters");
        }
        this.value = value.trim().toUpperCase();
    }
} 