package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
 
/**
 * Query to get technicians by skill.
 */
public record GetTechniciansBySkillQuery(SkillId skillId) {} 