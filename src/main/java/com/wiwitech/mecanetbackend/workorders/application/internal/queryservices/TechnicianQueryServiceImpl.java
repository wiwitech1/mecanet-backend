package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.queries.*;
import com.wiwitech.mecanetbackend.workorders.domain.services.TechnicianQueryService;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TechnicianQueryServiceImpl implements TechnicianQueryService {

    private final TechnicianRepository repository;

    public TechnicianQueryServiceImpl(TechnicianRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Technician> handle(GetTechnicianByIdQuery query) {
        return repository.findById(query.technicianId().getValue());
    }

    @Override
    public Optional<Technician> handle(GetTechnicianByIamUserIdQuery query) {
        return repository.findByIamUserIdValueAndTenantIdValue(
            query.iamUserId().getValue(), 
            TenantContext.getCurrentTenantId()
        );
    }

    @Override
    public List<Technician> handle(GetTechniciansByStatusQuery query) {
        return repository.findByCurrentStatusAndTenantIdValue(query.status(), TenantContext.getCurrentTenantId());
    }

    @Override
    public List<Technician> handle(GetTechniciansBySkillQuery query) {
        return repository.findBySkillAndTenantId(query.skillId(), TenantContext.getCurrentTenantId());
    }
} 