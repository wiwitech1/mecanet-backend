package com.wiwitech.mecanetbackend.workorders.interfaces.acl;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import java.util.Set;

/**
 * ACL port to validate technician skills against requirements.
 */
public interface CompetencyContextFacade {
    /** Throws exception if technician does not fulfill all skills */
    void validateSkills(TechnicianId technicianId, Set<SkillId> requiredSkills);
} 