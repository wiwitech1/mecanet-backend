package com.wiwitech.mecanetbackend.competency.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.resources.SkillResource;

public class SkillResourceFromEntityAssembler {
    public static SkillResource toResource(Skill s) {
        return new SkillResource(
                s.getId(),
                s.getName(),
                s.getDescription(),
                s.getCategory(),
                s.getStatus());
    }
}