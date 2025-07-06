package com.wiwitech.mecanetbackend.competency.interfaces.rest.resources;

import com.wiwitech.mecanetbackend.competency.domain.model.valueobjects.SkillStatus;

public record SkillResource(
        Long id,
        String name,
        String description,
        String category,
        SkillStatus status
) {}