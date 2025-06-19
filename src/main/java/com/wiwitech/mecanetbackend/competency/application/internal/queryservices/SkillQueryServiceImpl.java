package com.wiwitech.mecanetbackend.competency.application.internal.queryservices;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import com.wiwitech.mecanetbackend.competency.domain.model.queries.*;
import com.wiwitech.mecanetbackend.competency.domain.services.SkillQueryService;
import com.wiwitech.mecanetbackend.competency.infrastructure.persistence.jpa.repositories.SkillRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillQueryServiceImpl implements SkillQueryService {

    private final SkillRepository repository;

    public SkillQueryServiceImpl(SkillRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Skill> handle(GetAllSkillsQuery query) {
        Long tenant = TenantContext.getCurrentTenantId();
        return repository.findByTenantIdValue(tenant);
    }

    @Override
    public Optional<Skill> handle(GetSkillByIdQuery query) {
        Long tenant = TenantContext.getCurrentTenantId();
        return repository.findByIdAndTenantIdValue(query.skillId(), tenant);
    }
}