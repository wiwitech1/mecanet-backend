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
public class AttributeName {

    @Column(name = "attribute_name", length = 50)
    private String value;

    public AttributeName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Attribute name cannot exceed 50 characters");
        }
        this.value = value.trim().toLowerCase();
    }
} 