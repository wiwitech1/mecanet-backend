package com.wiwitech.mecanetbackend.subscription.domain.services;

import com.wiwitech.mecanetbackend.subscription.domain.model.queries.ValidateLimitQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionLimitType;

public interface SubscriptionLimitValidationService {
    
    /**
     * Valida si el tenant puede crear un nuevo recurso del tipo especificado
     * basándose en su plan de suscripción actual
     * 
     * @param query Query con tenantId y tipo de límite a validar
     * @return true si puede crear el recurso, false si excede el límite
     */
    boolean canCreateResource(ValidateLimitQuery query);
    
    /**
     * Obtiene el conteo actual de recursos del tipo especificado para el tenant
     * 
     * @param tenantId ID del tenant
     * @param limitType Tipo de límite a consultar
     * @return Número actual de recursos del tipo especificado
     */
    long getCurrentResourceCount(Long tenantId, SubscriptionLimitType limitType);
    
    /**
     * Obtiene el límite máximo permitido para el tipo de recurso especificado
     * basándose en el plan de suscripción del tenant
     * 
     * @param tenantId ID del tenant
     * @param limitType Tipo de límite a consultar
     * @return Límite máximo permitido
     */
    long getResourceLimit(Long tenantId, SubscriptionLimitType limitType);
} 