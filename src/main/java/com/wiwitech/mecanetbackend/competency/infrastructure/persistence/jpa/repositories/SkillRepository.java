package com.wiwitech.mecanetbackend.competency.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByNameAndTenantIdValue(String name, Long tenantId);
    List<Skill> findByTenantIdValue(Long tenantId);
    Optional<Skill> findByIdAndTenantIdValue(Long id, Long tenantId);
}