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
public class SubscriptionId {

    @Column(name = "subscription_id")
    private Long value;

    public SubscriptionId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("SubscriptionId value must be positive");
        }
        this.value = value;
    }
} 