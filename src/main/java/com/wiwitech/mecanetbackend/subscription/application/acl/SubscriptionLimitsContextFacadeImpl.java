package com.wiwitech.mecanetbackend.subscription.application.acl;

import com.wiwitech.mecanetbackend.subscription.domain.model.queries.ValidateLimitQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionLimitType;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionLimitValidationService;
import com.wiwitech.mecanetbackend.subscription.interfaces.acl.SubscriptionLimitsContextFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador ACL que implementa consultas directas a la base de datos
 * para obtener contadores reales de recursos por tenant
 */
@Slf4j
@Component("subscriptionLimitsContextFacade")
@RequiredArgsConstructor
public class SubscriptionLimitsContextFacadeImpl implements SubscriptionLimitsContextFacade {
    
    private final SubscriptionLimitValidationService subscriptionLimitValidationService;
    
    @Override
    public boolean canCreateResource(Long tenantId, SubscriptionLimitType limitType) {
        log.debug("Validando límite {} para tenant {}", limitType, tenantId);
        
        ValidateLimitQuery query = new ValidateLimitQuery(tenantId, limitType);
        boolean canCreate = subscriptionLimitValidationService.canCreateResource(query);
        
        log.debug("Resultado validación límite {} para tenant {}: {}", limitType, tenantId, canCreate);
        return canCreate;
    }
    
    @Override
    public long getCurrentResourceCount(Long tenantId, SubscriptionLimitType limitType) {
        log.debug("Obteniendo conteo actual de {} para tenant {}", limitType, tenantId);
        
        long count = subscriptionLimitValidationService.getCurrentResourceCount(tenantId, limitType);
        
        log.debug("Conteo actual de {} para tenant {}: {}", limitType, tenantId, count);
        return count;
    }
    
    @Override
    public long getResourceLimit(Long tenantId, SubscriptionLimitType limitType) {
        log.debug("Obteniendo límite de {} para tenant {}", limitType, tenantId);
        
        long limit = subscriptionLimitValidationService.getResourceLimit(tenantId, limitType);
        
        log.debug("Límite de {} para tenant {}: {}", limitType, tenantId, limit);
        return limit;
    }
} 