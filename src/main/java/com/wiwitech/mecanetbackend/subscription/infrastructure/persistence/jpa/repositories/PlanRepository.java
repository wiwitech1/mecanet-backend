package com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.domain.model.entities.PlanAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Plan repository interface for JPA operations
 * Provides database operations for Plan aggregate (global, not tenant-specific)
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    /**
     * Find all active plans
     * @return List of active plans
     */
    @Query("SELECT p FROM Plan p WHERE p.isActive = true")
    List<Plan> findAllByIsActiveTrue();
    
    /**
     * Find plan by ID only if active
     * @param id The plan ID
     * @return Optional plan if found and active
     */
    @Query("SELECT p FROM Plan p WHERE p.id = :id AND p.isActive = true")
    Optional<Plan> findByIdAndIsActiveTrue(@Param("id") Long id);
    
    /**
     * Check if plan exists by name
     * @param name The plan name
     * @return True if exists, false otherwise
     */
    @Query("SELECT COUNT(p) > 0 FROM Plan p WHERE p.name.value = :name")
    boolean existsByNameValue(@Param("name") String name);
    
    /**
     * Find plan by name only if active
     * @param name The plan name
     * @return Optional plan if found and active
     */
    @Query("SELECT p FROM Plan p WHERE p.name.value = :name AND p.isActive = true")
    Optional<Plan> findByNameValueAndIsActiveTrue(@Param("name") String name);
    
    /**
     * Find plan attributes by plan ID
     * @param planId The plan ID
     * @return List of plan attributes
     */
    @Query(value = "SELECT * FROM plan_attributes WHERE plan_id = :planId", nativeQuery = true)
    List<PlanAttribute> findAttributesByPlanId(@Param("planId") Long planId);
    
    /**
     * Count total plans (for admin purposes)
     * @return Total number of plans
     */
    long count();
    
    /**
     * Count active plans
     * @return Number of active plans
     */
    @Query("SELECT COUNT(p) FROM Plan p WHERE p.isActive = true")
    long countByIsActiveTrue();
} 