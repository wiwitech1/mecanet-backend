package com.wiwitech.mecanetbackend.competency.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class SkillId {

    @Column(name = "skill_id")
    private Long value;

    public SkillId(Long value) {
        if (value == null || value <= 0) throw new IllegalArgumentException("SkillId must be positive");
        this.value = value;
    }

    public Long getValue() { return value; }
    @Override public String toString() { return value.toString(); }
}