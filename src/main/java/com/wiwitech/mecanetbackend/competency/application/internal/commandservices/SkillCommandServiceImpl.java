package com.wiwitech.mecanetbackend.competency.application.internal.commandservices;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import com.wiwitech.mecanetbackend.competency.domain.model.commands.*;
import com.wiwitech.mecanetbackend.competency.domain.services.SkillCommandService;
import com.wiwitech.mecanetbackend.competency.infrastructure.persistence.jpa.repositories.SkillRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SkillCommandServiceImpl implements SkillCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(SkillCommandServiceImpl.class);
    private final SkillRepository repository;

    public SkillCommandServiceImpl(SkillRepository repository) {
        this.repository = repository;
    }

    /* ---------- CREATE ---------- */
    @Override
    @Transactional
    public Skill handle(CreateSkillCommand cmd) {
        Long tenant = requireTenant();

        if (repository.existsByNameAndTenantIdValue(cmd.name(), tenant))
            throw new IllegalArgumentException("Skill name already exists in tenant");

        Skill skill = Skill.create(cmd.name(), cmd.description(), cmd.category(), new TenantId(tenant));
        return repository.save(skill);
    }

    /* ---------- UPDATE ---------- */
    @Override
    @Transactional
    public Skill handle(UpdateSkillCommand cmd) {
        Long tenant = requireTenant();

        Skill skill = repository.findByIdAndTenantIdValue(cmd.skillId(), tenant)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        skill.update(cmd.name(), cmd.description(), cmd.category());
        return repository.save(skill);
    }

    /* ---------- DEACTIVATE ---------- */
    @Override
    @Transactional
    public Skill handle(DeactivateSkillCommand cmd) {
        Long tenant = requireTenant();

        Skill skill = repository.findByIdAndTenantIdValue(cmd.skillId(), tenant)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        skill.deactivate();
        return repository.save(skill);
    }

    /* ---------- SEED ---------- */
  
    /* ---------- helpers ---------- */
    private Long requireTenant() {
        Long t = TenantContext.getCurrentTenantId();
        if (t == null) throw new IllegalStateException("Tenant context missing");
        return t;
    }
}