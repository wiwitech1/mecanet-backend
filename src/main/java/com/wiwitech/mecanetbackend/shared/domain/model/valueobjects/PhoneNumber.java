package com.wiwitech.mecanetbackend.shared.domain.model.valueobjects;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(@Column(name = "phone") String value) {

    public PhoneNumber {
        if (value != null && !value.trim().isEmpty()) {
            if (value.length() < 7 || value.length() > 15) {
                throw new IllegalArgumentException("Phone number must be between 7 and 15 characters");
            }
        }
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}