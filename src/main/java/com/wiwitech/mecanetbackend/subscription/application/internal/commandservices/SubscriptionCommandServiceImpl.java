package com.wiwitech.mecanetbackend.subscription.application.internal.commandservices;

import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Subscription;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionCommandService;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.PlanId;
import com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionCommandServiceImpl.class);
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository, PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
    }

    /* ---------- CREATE ---------- */
    @Override
    @Transactional
    public Subscription handle(CreateSubscriptionCommand cmd) {
        LOG.info("Creating subscription for tenant: {} with plan: {}", cmd.tenantId(), cmd.planId());

        // Validar que el tenant no tenga ya una suscripción
        if (subscriptionRepository.existsByTenantIdValue(cmd.tenantId())) {
            throw new IllegalArgumentException("Tenant already has a subscription: " + cmd.tenantId());
        }

        // Validar que el plan existe y está activo
        planRepository.findByIdAndIsActiveTrue(cmd.planId())
                .orElseThrow(() -> new IllegalArgumentException("Active plan not found with ID: " + cmd.planId()));

        TenantId tenantId = new TenantId(cmd.tenantId());
        PlanId planId = new PlanId(cmd.planId());
        
        Subscription subscription = Subscription.create(tenantId, planId);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        
        LOG.info("Subscription created successfully with ID: {} for tenant: {}", 
                savedSubscription.getId(), cmd.tenantId());
        return savedSubscription;
    }

    /* ---------- CHANGE PLAN ---------- */
    @Override
    @Transactional
    public Subscription handle(ChangeSubscriptionPlanCommand cmd) {
        LOG.info("Changing subscription plan for tenant: {} to plan: {}", cmd.tenantId(), cmd.newPlanId());

        // Buscar la suscripción del tenant
        Subscription subscription = subscriptionRepository.findByTenantIdValue(cmd.tenantId())
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found for tenant: " + cmd.tenantId()));

        // Validar que el nuevo plan existe y está activo
        planRepository.findByIdAndIsActiveTrue(cmd.newPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Active plan not found with ID: " + cmd.newPlanId()));

        // Cambiar el plan
        PlanId newPlanId = new PlanId(cmd.newPlanId());
        subscription.changePlan(newPlanId);
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        
        LOG.info("Subscription plan changed successfully for tenant: {} to plan: {}", 
                cmd.tenantId(), cmd.newPlanId());
        return savedSubscription;
    }

    /* ---------- HELPERS ---------- */
    private Long requireTenant() {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context missing");
        }
        return tenantId;
    }
} 