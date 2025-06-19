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
public class TechnicianId {
    @Column(name = "technician_id_ref")
    private Long value;

    public TechnicianId(Long value) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("TechnicianId must be positive");
        this.value = value;
    }

    @Override public String toString() { return value.toString(); }
}