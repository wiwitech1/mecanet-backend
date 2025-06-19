package com.wiwitech.mecanetbackend.competency.domain.services;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import com.wiwitech.mecanetbackend.competency.domain.model.commands.*;

public interface SkillCommandService {
    Skill handle(CreateSkillCommand cmd);
    Skill handle(UpdateSkillCommand cmd);
    Skill handle(DeactivateSkillCommand cmd);
}