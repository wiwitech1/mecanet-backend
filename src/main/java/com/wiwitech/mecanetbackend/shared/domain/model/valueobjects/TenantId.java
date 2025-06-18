package com.wiwitech.mecanetbackend.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class TenantId {

    @Column(nullable = false)
    private final Long value;

    public TenantId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Tenant ID must be a positive number");
        }
        this.value = value;
    }

    public TenantId() {
        // Constructor por defecto para JPA
        this.value = 1L; // Valor por defecto
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantId tenantId = (TenantId) o;
        return value.equals(tenantId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
