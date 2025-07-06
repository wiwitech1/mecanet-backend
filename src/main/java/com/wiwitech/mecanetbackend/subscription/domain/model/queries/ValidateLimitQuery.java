package com.wiwitech.mecanetbackend.subscription.domain.model.queries;

import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionLimitType;

/**
 * Query para validar límites de suscripción
 */
public record ValidateLimitQuery(
    Long tenantId,
    SubscriptionLimitType limitType
) {
    
    public ValidateLimitQuery {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("tenantId debe ser un valor positivo");
        }
        if (limitType == null) {
            throw new IllegalArgumentException("limitType no puede ser null");
        }
    }
} 