package com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources;

import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionStatus;
import java.time.LocalDateTime;

/**
 * Subscription resource for API responses
 */
public record SubscriptionResource(
        Long id,
        Long tenantId,
        Long planId,
        SubscriptionStatus status,
        LocalDateTime subscribedAt,
        LocalDateTime expiresAt,
        Boolean autoRenew
) {} 