package com.wiwitech.mecanetbackend.subscription.domain.model.aggregates;

import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.subscription.domain.model.events.SubscriptionCreatedEvent;
import com.wiwitech.mecanetbackend.subscription.domain.model.events.SubscriptionPlanChangedEvent;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.PlanId;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "tenant_id"))
    private TenantId tenantId;

    @Embedded
    private PlanId planId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatus status;

    @Column(name = "subscribed_at")
    private LocalDateTime subscribedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "auto_renew")
    private Boolean autoRenew = false;

    private Subscription(TenantId tenantId, PlanId planId) {
        this.tenantId = tenantId;
        this.planId = planId;
        this.status = SubscriptionStatus.ACTIVE;
        this.subscribedAt = LocalDateTime.now();
        this.autoRenew = false;
    }

    public static Subscription create(TenantId tenantId, PlanId planId) {
        Subscription subscription = new Subscription(tenantId, planId);
        subscription.addDomainEvent(new SubscriptionCreatedEvent(
            subscription.getTenantId().getValue(),
            subscription.getPlanId().getValue()
        ));
        return subscription;
    }

    public void changePlan(PlanId newPlanId) {
        if (newPlanId.equals(this.planId)) {
            throw new IllegalArgumentException("New plan must be different from current plan");
        }
        
        PlanId oldPlanId = this.planId;
        this.planId = newPlanId;
        this.subscribedAt = LocalDateTime.now();
        
        this.addDomainEvent(new SubscriptionPlanChangedEvent(
            this.tenantId.getValue(),
            oldPlanId.getValue(),
            newPlanId.getValue()
        ));
    }

    public void activate() {
        if (isActive()) {
            throw new IllegalStateException("Subscription is already active");
        }
        this.status = SubscriptionStatus.ACTIVE;
        this.subscribedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (!isActive()) {
            throw new IllegalStateException("Subscription is already inactive");
        }
        this.status = SubscriptionStatus.INACTIVE;
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.expiresAt = LocalDateTime.now();
    }

    public void setExpirationDate(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void enableAutoRenew() {
        this.autoRenew = true;
    }

    public void disableAutoRenew() {
        this.autoRenew = false;
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && !isExpired();
    }

    public boolean isExpired() {
        return status == SubscriptionStatus.EXPIRED || 
               (expiresAt != null && LocalDateTime.now().isAfter(expiresAt));
    }

    public boolean isInactive() {
        return status == SubscriptionStatus.INACTIVE;
    }

    public boolean hasAutoRenew() {
        return autoRenew;
    }

    public Long getTenantIdValue() {
        return tenantId.getValue();
    }

    public Long getPlanIdValue() {
        return planId.getValue();
    }
} 