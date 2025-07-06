package com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear una planta pero se excede el límite del plan de suscripción
 */
public class PlantLimitExceededException extends RuntimeException {
    
    private final Long tenantId;
    private final long currentCount;
    private final long limit;
    
    public PlantLimitExceededException(Long tenantId, long currentCount, long limit) {
        super(String.format("No se puede crear planta. Límite excedido para tenant %d. Actual: %d, Límite: %d", 
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