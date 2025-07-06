package com.wiwitech.mecanetbackend.subscription.application.internal.commandservices;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.*;
import com.wiwitech.mecanetbackend.subscription.domain.services.PlanCommandService;
import com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PlanCommandServiceImpl implements PlanCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanCommandServiceImpl.class);
    private final PlanRepository repository;

    public PlanCommandServiceImpl(PlanRepository repository) {
        this.repository = repository;
    }

    /* ---------- CREATE ---------- */
    @Override
    @Transactional
    public Plan handle(CreatePlanCommand cmd) {
        LOG.info("Creating plan with name: {}", cmd.name());

        if (repository.existsByNameValue(cmd.name().toUpperCase())) {
            throw new IllegalArgumentException("Plan name already exists: " + cmd.name());
        }

        Plan plan = Plan.create(cmd.name(), cmd.description(), cmd.cost());
        Plan savedPlan = repository.save(plan);
        
        LOG.info("Plan created successfully with ID: {}", savedPlan.getId());
        return savedPlan;
    }

    /* ---------- UPDATE ---------- */
    @Override
    @Transactional
    public Plan handle(UpdatePlanCommand cmd) {
        LOG.info("Updating plan with ID: {}", cmd.planId());

        Plan plan = repository.findByIdAndIsActiveTrue(cmd.planId())
                .orElseThrow(() -> new IllegalArgumentException("Active plan not found with ID: " + cmd.planId()));

        plan.updateInfo(cmd.description(), cmd.cost());
        Plan savedPlan = repository.save(plan);
        
        LOG.info("Plan updated successfully with ID: {}", savedPlan.getId());
        return savedPlan;
    }

    /* ---------- DEACTIVATE ---------- */
    @Override
    @Transactional
    public Plan handle(DeactivatePlanCommand cmd) {
        LOG.info("Deactivating plan with ID: {}", cmd.planId());

        Plan plan = repository.findByIdAndIsActiveTrue(cmd.planId())
                .orElseThrow(() -> new IllegalArgumentException("Active plan not found with ID: " + cmd.planId()));

        plan.deactivate();
        Plan savedPlan = repository.save(plan);
        
        LOG.info("Plan deactivated successfully with ID: {}", savedPlan.getId());
        return savedPlan;
    }

    /* ---------- SEED ---------- */
    @Override
    @Transactional
    public void handle(SeedPlansCommand cmd) {
        LOG.info("Starting plans seeding verification");

        seedPlanIfNotExists("FREE", "Plan gratuito básico", BigDecimal.ZERO);
        seedPlanIfNotExists("PROFESSIONAL", "Plan profesional con funcionalidades avanzadas", BigDecimal.valueOf(29.99));
        seedPlanIfNotExists("CORPORATE", "Plan corporativo con funcionalidades completas", BigDecimal.valueOf(99.99));

        LOG.info("Plans seeding verification completed");
    }

    /* ---------- HELPERS ---------- */
    private void seedPlanIfNotExists(String name, String description, BigDecimal cost) {
        if (!repository.existsByNameValue(name)) {
            LOG.info("Creating default plan: {}", name);
            Plan plan = Plan.create(name, description, cost);
            
            // Agregar atributos según el plan
            addDefaultAttributes(plan, name);
            
            repository.save(plan);
            LOG.info("Default plan created: {}", name);
        } else {
            LOG.info("Plan already exists: {}", name);
        }
    }

    private void addDefaultAttributes(Plan plan, String planName) {
        switch (planName) {
            case "FREE":
                plan.addAttribute("max_machines", 2);
                plan.addAttribute("max_plants", 1);
                plan.addAttribute("max_users", 3);
                plan.addAttribute("max_production_lines", 2);
                break;
            case "PROFESSIONAL":
                plan.addAttribute("max_machines", 20);
                plan.addAttribute("max_plants", 3);
                plan.addAttribute("max_users", 10);
                plan.addAttribute("max_production_lines", 10);
                break;
            case "CORPORATE":
                plan.addAttribute("max_machines", Integer.MAX_VALUE); // Unlimited
                plan.addAttribute("max_plants", Integer.MAX_VALUE); // Unlimited
                plan.addAttribute("max_users", Integer.MAX_VALUE); // Unlimited
                plan.addAttribute("max_production_lines", Integer.MAX_VALUE); // Unlimited
                break;
        }
    }
} 