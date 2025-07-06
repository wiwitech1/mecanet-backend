package com.wiwitech.mecanetbackend.iam.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un usuario pero se excede el límite del plan de suscripción
 */
public class UserLimitExceededException extends RuntimeException {
    
    private final Long tenantId;
    private final long currentCount;
    private final long limit;
    
    public UserLimitExceededException(Long tenantId, long currentCount, long limit) {
        super(String.format("No se puede crear usuario. Límite excedido para tenant %d. Actual: %d, Límite: %d", 
            tenantId, currentCount, limit));
        this.tenantId = tenantId;
        this.currentCount = currentCount;
        this.limit = limit;
    }
    
    public Long getTenantId() {
        return tenantId;
    }
    
    public long getCurrentCount() {
        return currentCount;
    }
    
    public long getLimit() {
        return limit;
    }
} 