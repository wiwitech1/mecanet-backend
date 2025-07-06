package com.wiwitech.mecanetbackend.subscription.application.internal.queryservices;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.domain.model.entities.PlanAttribute;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetAllActivePlansQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanAttributesQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanByIdQuery;
import com.wiwitech.mecanetbackend.subscription.domain.services.PlanQueryService;
import com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanQueryServiceImpl implements PlanQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanQueryServiceImpl.class);
    private final PlanRepository repository;

    public PlanQueryServiceImpl(PlanRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Plan> handle(GetAllActivePlansQuery query) {
        LOG.debug("Getting all active plans");
        List<Plan> plans = repository.findAllByIsActiveTrue();
        LOG.debug("Found {} active plans", plans.size());
        return plans;
    }

    @Override
    public Optional<Plan> handle(GetPlanByIdQuery query) {
        LOG.debug("Getting plan by ID: {}", query.planId());
        Optional<Plan> plan = repository.findByIdAndIsActiveTrue(query.planId());
        if (plan.isPresent()) {
            LOG.debug("Plan found with ID: {}", query.planId());
        } else {
            LOG.debug("Plan not found with ID: {}", query.planId());
        }
        return plan;
    }

    @Override
    public List<PlanAttribute> handle(GetPlanAttributesQuery query) {
        LOG.debug("Getting attributes for plan ID: {}", query.planId());
        
        // Primero verificar que el plan existe y está activo
        repository.findByIdAndIsActiveTrue(query.planId())
                .orElseThrow(() -> new IllegalArgumentException("Active plan not found with ID: " + query.planId()));
        
        List<PlanAttribute> attributes = repository.findAttributesByPlanId(query.planId());
        LOG.debug("Found {} attributes for plan ID: {}", attributes.size(), query.planId());
        return attributes;
    }
} 