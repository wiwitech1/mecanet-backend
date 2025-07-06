package com.wiwitech.mecanetbackend.subscription.application.internal.queryservices;

import com.wiwitech.mecanetbackend.subscription.domain.model.queries.ValidateLimitQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionLimitType;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionLimitValidationService;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionQueryService;
import com.wiwitech.mecanetbackend.subscription.domain.services.PlanQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementación del domain service para validación de límites de suscripción
 * Utiliza consultas directas a la base de datos para obtener contadores reales
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionLimitValidationServiceImpl implements SubscriptionLimitValidationService {
    
    private final SubscriptionQueryService subscriptionQueryService;
    private final PlanQueryService planQueryService;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public boolean canCreateResource(ValidateLimitQuery query) {
        log.debug("Validando si se puede crear recurso {} para tenant {}", query.limitType(), query.tenantId());
        
        long currentCount = getCurrentResourceCount(query.tenantId(), query.limitType());
        long limit = getResourceLimit(query.tenantId(), query.limitType());
        
        boolean canCreate = currentCount < limit;
        
        log.debug("Conteo actual: {}, Límite: {}, Puede crear: {}", currentCount, limit, canCreate);
        return canCreate;
    }
    
    @Override
    public long getCurrentResourceCount(Long tenantId, SubscriptionLimitType limitType) {
        log.debug("Obteniendo conteo actual de {} para tenant {}", limitType, tenantId);
        
        String sql = buildCountQuery(limitType);
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tenantId);
        
        long result = count != null ? count : 0L;
        log.debug("Conteo actual de {} para tenant {}: {}", limitType, tenantId, result);
        return result;
    }
    
    @Override
    public long getResourceLimit(Long tenantId, SubscriptionLimitType limitType) {
        log.debug("Obteniendo límite de {} para tenant {}", limitType, tenantId);
        
        // Obtener el plan de suscripción del tenant
        var subscriptionQuery = new com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetSubscriptionByTenantQuery(tenantId);
        var subscription = subscriptionQueryService.handle(subscriptionQuery);
        
        if (subscription.isEmpty()) {
            log.warn("No se encontró suscripción para tenant {}", tenantId);
            return 0L;
        }
        
        // Obtener el límite del plan
        var planQuery = new com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanByIdQuery(subscription.get().getPlanIdValue());
        var plan = planQueryService.handle(planQuery);
        
        if (plan.isEmpty()) {
            log.warn("No se encontró plan para suscripción del tenant {}", tenantId);
            return 0L;
        }
        
        // Obtener todos los atributos del plan y buscar el específico
        var attributesQuery = new com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanAttributesQuery(plan.get().getId());
        var attributes = planQueryService.handle(attributesQuery);
        
        var targetAttribute = attributes.stream()
            .filter(attr -> attr.getAttributeNameValue().equals(limitType.getAttributeName()))
            .findFirst();
        
        if (targetAttribute.isEmpty()) {
            log.warn("No se encontró atributo {} para plan del tenant {}", limitType.getAttributeName(), tenantId);
            return 0L;
        }
        
        long limit = targetAttribute.get().getAttributeValueValue();
        log.debug("Límite de {} para tenant {}: {}", limitType, tenantId, limit);
        return limit;
    }
    
    /**
     * Construye la consulta SQL para contar recursos según el tipo de límite
     */
    private String buildCountQuery(SubscriptionLimitType limitType) {
        return switch (limitType) {
            case MAX_USERS -> "SELECT COUNT(*) FROM users WHERE tenant_id = ? AND active = true";
            case MAX_PLANTS -> "SELECT COUNT(*) FROM plants WHERE tenant_id = ? AND active = true";
            case MAX_MACHINES -> "SELECT COUNT(*) FROM machines WHERE tenant_id = ? AND status = 'OPERATIONAL'";
            case MAX_PRODUCTION_LINES -> "SELECT COUNT(*) FROM production_lines WHERE tenant_id = ? AND status = 'READY'";
        };
    }
}