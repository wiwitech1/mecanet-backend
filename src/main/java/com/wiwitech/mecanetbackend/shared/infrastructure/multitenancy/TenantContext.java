package com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Thread-safe context holder for tenant information
 * This class provides a way to store and retrieve tenant information
 * for the current thread/request context across all bounded contexts
 */
public class TenantContext {
    
    private static final ThreadLocal<Long> CURRENT_TENANT_ID = new ThreadLocal<>();
    private static final Long DEFAULT_TENANT_ID = 1L;
    
    private TenantContext() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Set the tenant ID for current thread
     * @param tenantId the tenant ID
     */
    public static void setCurrentTenantId(Long tenantId) {
        CURRENT_TENANT_ID.set(tenantId != null ? tenantId : DEFAULT_TENANT_ID);
        log.info("Setting tenant ID to {}", tenantId);
    }
    
    /**
     * Get the current tenant ID
     * @return current tenant ID or default if not set
     */
    public static Long getCurrentTenantId() {
        return CURRENT_TENANT_ID.get() != null ? CURRENT_TENANT_ID.get() : DEFAULT_TENANT_ID;
    }
    
    /**
     * Clear the tenant context for current thread
     * Should be called at the end of request processing
     */
    public static void clear() {
        CURRENT_TENANT_ID.remove();
    }
    
    /**
     * Check if a tenant is currently set
     * @return true if tenant is set, false otherwise
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT_ID.get() != null;
    }
} 