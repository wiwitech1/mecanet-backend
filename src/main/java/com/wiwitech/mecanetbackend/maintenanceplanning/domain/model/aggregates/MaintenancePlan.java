package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanStatus;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanType;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanPeriod;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "maintenance_plans")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "plan_type")
@NoArgsConstructor
public abstract class MaintenancePlan extends AuditableAbstractAggregateRoot<MaintenancePlan> {

    @Column(nullable = false, length = 120)
    protected String name;

    @Embedded
    protected PlanPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected PlanStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", insertable = false, updatable = false)
    protected PlanType planType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "tenant_id", nullable = false))
    protected TenantId tenantId;

    protected MaintenancePlan(String name,
                              PlanPeriod period,
                              PlanType type,
                              TenantId tenantId) {
        this.name     = name;
        this.period   = period;
        this.status   = PlanStatus.ACTIVE;
        this.planType = type;
        this.tenantId = tenantId;
    }

    public boolean isActiveOn(LocalDate date) {
        return status == PlanStatus.ACTIVE && period.contains(date);
    }

    public void deactivate() { this.status = PlanStatus.INACTIVE; }
}