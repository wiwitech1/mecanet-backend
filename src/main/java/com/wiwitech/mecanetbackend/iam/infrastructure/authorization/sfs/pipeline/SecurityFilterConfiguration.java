package com.wiwitech.mecanetbackend.iam.infrastructure.authorization.sfs.pipeline;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Configuration for security filters to ensure proper order of execution
 * The TenantExtractionFilter should run early in the filter chain
 */
@Configuration
public class SecurityFilterConfiguration {

    /**
     * Register TenantExtractionFilter with highest priority
     * This ensures tenant context is available for subsequent filters and processing
     */
    @Bean
    public FilterRegistrationBean<TenantExtractionFilter> tenantExtractionFilterRegistration(
            TenantExtractionFilter tenantExtractionFilter) {
        
        FilterRegistrationBean<TenantExtractionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(tenantExtractionFilter);
        registration.addUrlPatterns("/api/*"); // Solo aplicar a rutas de API
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // Máxima prioridad
        registration.setName("TenantExtractionFilter");
        return registration;
    }
} 