package com.wiwitech.mecanetbackend.subscription.domain.model.aggregates;

import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.subscription.domain.model.entities.PlanAttribute;
import com.wiwitech.mecanetbackend.subscription.domain.model.events.PlanCreatedEvent;
import com.wiwitech.mecanetbackend.subscription.domain.model.events.PlanUpdatedEvent;
import com.wiwitech.mecanetbackend.subscription.domain.model.events.PlanDeactivatedEvent;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.PlanCost;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.PlanName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor
public class Plan extends AuditableAbstractAggregateRoot<Plan> {

    @Embedded
    private PlanName name;

    @Column(name = "description")
    private String description;

    @Embedded
    private PlanCost cost;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PlanAttribute> attributes = new HashSet<>();

    private Plan(String name, String description, BigDecimal cost) {
        this.name = new PlanName(name);
        this.description = description;
        this.cost = new PlanCost(cost);
        this.isActive = true;
    }

    public static Plan create(String name, String description, BigDecimal cost) {
        Plan plan = new Plan(name, description, cost);
        plan.addDomainEvent(new PlanCreatedEvent(plan.getId(), plan.getName().getValue()));
        return plan;
    }

    public void addAttribute(String attributeName, Integer attributeValue) {
        if (getAttributeByName(attributeName).isPresent()) {
            throw new IllegalArgumentException("Attribute with name '" + attributeName + "' already exists");
        }
        
        PlanAttribute attribute = new PlanAttribute(this, attributeName, attributeValue);
        this.attributes.add(attribute);
    }

    public void updateAttribute(String attributeName, Integer newValue) {
        PlanAttribute attribute = getAttributeByName(attributeName)
            .orElseThrow(() -> new IllegalArgumentException("Attribute with name '" + attributeName + "' not found"));
        
        attribute.updateValue(newValue);
        this.addDomainEvent(new PlanUpdatedEvent(this.getId(), this.getName().getValue()));
    }

    public void removeAttribute(String attributeName) {
        Optional<PlanAttribute> attribute = getAttributeByName(attributeName);
        if (attribute.isPresent()) {
            this.attributes.remove(attribute.get());
            this.addDomainEvent(new PlanUpdatedEvent(this.getId(), this.getName().getValue()));
        }
    }

    public void updateInfo(String description, BigDecimal cost) {
        this.description = description;
        this.cost = new PlanCost(cost);
        this.addDomainEvent(new PlanUpdatedEvent(this.getId(), this.getName().getValue()));
    }

    public void activate() {
        if (isActive) {
            throw new IllegalStateException("Plan is already active");
        }
        this.isActive = true;
        this.addDomainEvent(new PlanUpdatedEvent(this.getId(), this.getName().getValue()));
    }

    public void deactivate() {
        if (!isActive) {
            throw new IllegalStateException("Plan is already inactive");
        }
        this.isActive = false;
        this.addDomainEvent(new PlanDeactivatedEvent(this.getId(), this.getName().getValue()));
    }

    public Optional<PlanAttribute> getAttributeByName(String attributeName) {
        return attributes.stream()
            .filter(attr -> attr.getAttributeNameValue().equals(attributeName.toLowerCase()))
            .findFirst();
    }

    public Integer getAttributeValue(String attributeName) {
        return getAttributeByName(attributeName)
            .map(PlanAttribute::getAttributeValueValue)
            .orElse(0);
    }

    public boolean hasAttribute(String attributeName) {
        return getAttributeByName(attributeName).isPresent();
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFree() {
        return cost.isFree();
    }

    public String getNameValue() {
        return name.getValue();
    }

    public BigDecimal getCostValue() {
        return cost.getValue();
    }
} 