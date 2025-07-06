package com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Subscription;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.SubscriptionResource;

/**
 * Assembler to convert Subscription entity to SubscriptionResource
 */
public class SubscriptionResourceFromEntityAssembler {
    
    public static SubscriptionResource toResource(Subscription subscription) {
        return new SubscriptionResource(
                subscription.getId(),
                subscription.getTenantIdValue(),
                subscription.getPlanIdValue(),
                subscription.getStatus(),
                subscription.getSubscribedAt(),
                subscription.getExpiresAt(),
                subscription.hasAutoRenew()
        );
    }
} 