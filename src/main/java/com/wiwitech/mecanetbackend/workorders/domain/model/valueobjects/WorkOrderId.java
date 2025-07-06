package com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class WorkOrderId {

    @Column(name = "work_order_id", nullable = false, unique = true)
    private Long value;

    public WorkOrderId(Long value) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("WorkOrderId must be positive");
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
} 