package com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy;

import org.springframework.stereotype.Component;

/**
 * Helper class to facilitate usage of TenantContext in services and repositories
 * Provides convenient methods to work with tenant information across bounded contexts
 */
@Component
public class TenantContextHelper {
    
    /**
     * Get current tenant ID for queries and operations
     * @return current tenant ID
     */
    public Long getCurrentTenantId() {
        return TenantContext.getCurrentTenantId();
    }
    
    /**
     * Check if current request has a valid tenant context
     * @return true if tenant context is set, false otherwise
     */
    public boolean hasValidTenantContext() {
        return TenantContext.hasTenant();
    }
    
    /**
     * Execute a block of code with a specific tenant context
     * Useful for testing or administrative operations
     * @param tenantId tenant ID to use
     * @param operation operation to execute
     * @param <T> return type
     * @return result of the operation
     */
    public <T> T executeInTenantContext(Long tenantId, TenantOperation<T> operation) {
        // Guardar contexto actual
        Long currentTenantId = TenantContext.getCurrentTenantId();
        
        try {
            // Establecer nuevo contexto
            TenantContext.setCurrentTenantId(tenantId);
            return operation.execute();
        } finally {
            // Restaurar contexto original
            TenantContext.setCurrentTenantId(currentTenantId);
        }
    }
    
    /**
     * Functional interface for operations that need to run in a specific tenant context
     * @param <T> return type
     */
    @FunctionalInterface
    public interface TenantOperation<T> {
        T execute();
    }
    
    /**
     * Log current tenant context information (useful for debugging)
     * @return formatted string with tenant information
     */
    public String getTenantContextInfo() {
        return String.format("Current Tenant: %d, Has Context: %b", 
                getCurrentTenantId(), hasValidTenantContext());
    }
} 