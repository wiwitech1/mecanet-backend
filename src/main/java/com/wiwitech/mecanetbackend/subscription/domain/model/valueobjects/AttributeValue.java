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
public class AttributeValue {

    @Column(name = "attribute_value")
    private Integer value;

    public AttributeValue(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Attribute value cannot be null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Attribute value cannot be negative");
        }
        this.value = value;
    }

    public boolean isUnlimited() {
        return value == Integer.MAX_VALUE;
    }

    public boolean isZero() {
        return value == 0;
    }

    public static AttributeValue unlimited() {
        return new AttributeValue(Integer.MAX_VALUE);
    }

    public static AttributeValue zero() {
        return new AttributeValue(0);
    }
} 