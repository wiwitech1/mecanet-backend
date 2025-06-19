package com.wiwitech.mecanetbackend.competency.domain.services;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import com.wiwitech.mecanetbackend.competency.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface SkillQueryService {
    List<Skill> handle(GetAllSkillsQuery query);
    Optional<Skill> handle(GetSkillByIdQuery query);
}