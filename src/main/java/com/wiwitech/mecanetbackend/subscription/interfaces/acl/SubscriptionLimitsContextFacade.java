package com.wiwitech.mecanetbackend.subscription.interfaces.acl;

import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionLimitType;

/**
 * Puerto ACL para consultas de límites de suscripción
 * Permite a otros BCs consultar límites sin conocer la estructura interna de Subscription BC
 */
public interface SubscriptionLimitsContextFacade {
    
    /**
     * Valida si el tenant puede crear un nuevo recurso del tipo especificado
     * 
     * @param tenantId ID del tenant
     * @param limitType Tipo de límite a validar
     * @return true si puede crear el recurso, false si excede el límite
     */
    boolean canCreateResource(Long tenantId, SubscriptionLimitType limitType);
    
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
     * 
     * @param tenantId ID del tenant
     * @param limitType Tipo de límite a consultar
     * @return Límite máximo permitido
     */
    long getResourceLimit(Long tenantId, SubscriptionLimitType limitType);
} 