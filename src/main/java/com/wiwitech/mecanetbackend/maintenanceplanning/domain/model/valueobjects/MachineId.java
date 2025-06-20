package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class MachineId {

    @Column(name = "machine_id")
    private Long value;

    public MachineId(Long value) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("MachineId inválido");
        this.value = value;
    }
}