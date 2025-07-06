package com.wiwitech.mecanetbackend.subscription.domain.model.entities;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.AttributeName;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.AttributeValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plan_attributes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"plan_id", "attribute_name"})
})
@Getter
@NoArgsConstructor
public class PlanAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Embedded
    private AttributeName attributeName;

    @Embedded
    private AttributeValue attributeValue;

    public PlanAttribute(Plan plan, String attributeName, Integer attributeValue) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan cannot be null");
        }
        this.plan = plan;
        this.attributeName = new AttributeName(attributeName);
        this.attributeValue = new AttributeValue(attributeValue);
    }

    public void updateValue(Integer newValue) {
        this.attributeValue = new AttributeValue(newValue);
    }

    public String getAttributeNameValue() {
        return attributeName.getValue();
    }

    public Integer getAttributeValueValue() {
        return attributeValue.getValue();
    }

    public boolean isUnlimited() {
        return attributeValue.isUnlimited();
    }

    public boolean isZero() {
        return attributeValue.isZero();
    }
} 