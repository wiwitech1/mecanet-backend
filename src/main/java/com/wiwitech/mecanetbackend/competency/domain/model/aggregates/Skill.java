package com.wiwitech.mecanetbackend.competency.domain.model.aggregates;

import com.wiwitech.mecanetbackend.competency.domain.model.events.*;
import com.wiwitech.mecanetbackend.competency.domain.model.valueobjects.SkillStatus;
import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "skills",
       uniqueConstraints = @UniqueConstraint(columnNames = {"name", "tenant_id"}))
@NoArgsConstructor
public class Skill extends AuditableAbstractAggregateRoot<Skill> {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillStatus status;

    /* multitenancy */
    @Embedded
    @AttributeOverride(name = "value",
        column = @Column(name = "tenant_id", nullable = false))
    private TenantId tenantId;

    /* ---------- Factory ---------- */
    public static Skill create(String name, String description,
                               String category, TenantId tenantId) {
        Skill s = new Skill();
        s.name        = name;
        s.description = description;
        s.category    = category;
        s.status      = SkillStatus.ACTIVE;
        s.tenantId    = tenantId;
        s.addDomainEvent(new SkillCreatedEvent(s.getId(), tenantId.getValue(), name));
        return s;
    }

    /* ---------- Behaviour ---------- */
    public void update(String name, String description, String category) {
        this.name        = name;
        this.description = description;
        this.category    = category;
        addDomainEvent(new SkillUpdatedEvent(getId(), tenantId.getValue()));
    }

    public void deactivate() {
        this.status = SkillStatus.INACTIVE;
        addDomainEvent(new SkillDeactivatedEvent(getId(), tenantId.getValue()));
    }

    /* ---------- Convenience ---------- */
    public boolean isActive() { return status == SkillStatus.ACTIVE; }
}