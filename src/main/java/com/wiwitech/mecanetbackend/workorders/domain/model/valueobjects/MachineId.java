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
public class MachineId {

    @Column(name = "machine_id", nullable = false)
    private Long value;

    public MachineId(Long value) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("MachineId must be positive");
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
} 