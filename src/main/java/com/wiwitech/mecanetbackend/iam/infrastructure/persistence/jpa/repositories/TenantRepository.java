package com.wiwitech.mecanetbackend.iam.infrastructure.persistence.jpa.repositories;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wiwitech.mecanetbackend.iam.domain.model.aggregates.Tenant;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
/**
 * Tenant Repository
 * This repository provides data access operations for Tenant entities
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    /**
     * Find tenant by RUC
     * @param ruc the RUC to search for
     * @return Optional containing the tenant if found
     */
    Optional<Tenant> findByRuc(String ruc);
    
    /**
     * Check if tenant exists by RUC
     * @param ruc the RUC to check
     * @return true if tenant exists, false otherwise
     */
    boolean existsByRuc(String ruc);
    

    boolean existsByEmail(EmailAddress email);
    
    /**
     * Find tenant by legal name
     * @param legalName the legal name to search for
     * @return Optional containing the tenant if found
     */
    Optional<Tenant> findByLegalName(String legalName);
    
    /**
     * Find tenant by email value
     * @param value the email value to search for
     * @return Optional containing the tenant if found
     */
    Optional<Tenant> findByEmailValue(String value);
} 