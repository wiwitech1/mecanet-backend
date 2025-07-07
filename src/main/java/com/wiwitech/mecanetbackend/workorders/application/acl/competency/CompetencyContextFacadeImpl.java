package com.wiwitech.mecanetbackend.workorders.application.acl.competency;

import com.wiwitech.mecanetbackend.workorders.interfaces.acl.CompetencyContextFacade;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component("workordersCompetencyAcl")
@RequiredArgsConstructor
public class CompetencyContextFacadeImpl implements CompetencyContextFacade {

    private final TechnicianRepository technicianRepository;

    @Override
    public void validateSkills(TechnicianId technicianId, Set<SkillId> requiredSkills) {
        log.info("[ACL] Validating skills for technician {}", technicianId);
        
        Long tenantId = TenantContext.getCurrentTenantId();
        
        // Buscar el técnico
        var technician = technicianRepository.findByIamUserIdValueAndTenantIdValue(
                technicianId.getValue(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Technician not found with ID: " + technicianId.getValue()));
        
        // Verificar que tiene todas las skills requeridas
        Set<SkillId> technicianSkills = technician.getSkills();
        
        for (SkillId requiredSkill : requiredSkills) {
            if (!technicianSkills.contains(requiredSkill)) {
                throw new IllegalArgumentException(
                        "Technician " + technicianId.getValue() + 
                        " lacks required skill: " + requiredSkill.value());
            }
        }
        
        log.info("[ACL] Technician {} validated successfully - has all {} required skills", 
                technicianId.getValue(), requiredSkills.size());
    }
} 