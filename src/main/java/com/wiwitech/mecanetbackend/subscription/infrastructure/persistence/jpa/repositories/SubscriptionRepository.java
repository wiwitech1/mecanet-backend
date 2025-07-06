package com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Subscription;
import com.wiwitech.mecanetbackend.subscription.domain.model.valueobjects.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Subscription repository interface for JPA operations
 * Provides multi-tenant aware database operations for Subscription aggregate
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    /**
     * Find subscription by tenant ID
     * @param tenantId The tenant ID
     * @return Optional subscription if found
     */
    @Query("SELECT s FROM Subscription s WHERE s.tenantId.value = :tenantId")
    Optional<Subscription> findByTenantIdValue(@Param("tenantId") Long tenantId);
    
    /**
     * Find active subscription by tenant ID
     * @param tenantId The tenant ID
     * @return Optional active subscription if found
     */
    @Query("SELECT s FROM Subscription s WHERE s.tenantId.value = :tenantId AND s.status = 'ACTIVE'")
    Optional<Subscription> findActiveByTenantIdValue(@Param("tenantId") Long tenantId);
    
    /**
     * Find subscriptions by plan ID
     * @param planId The plan ID
     * @return List of subscriptions for the plan
     */
    @Query("SELECT s FROM Subscription s WHERE s.planId.value = :planId")
    List<Subscription> findByPlanIdValue(@Param("planId") Long planId);
    
    /**
     * Find subscriptions by status
     * @param status The subscription status
     * @return List of subscriptions with the status
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = :status")
    List<Subscription> findByStatus(@Param("status") SubscriptionStatus status);
    
    /**
     * Find subscriptions that are about to expire
     * @param dateTime The reference date time
     * @return List of subscriptions expiring before the given date
     */
    @Query("SELECT s FROM Subscription s WHERE s.expiresAt <= :dateTime AND s.status = 'ACTIVE'")
    List<Subscription> findExpiringBefore(@Param("dateTime") LocalDateTime dateTime);
    
    /**
     * Find subscriptions with auto-renew enabled
     * @return List of subscriptions with auto-renew enabled
     */
    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = true AND s.status = 'ACTIVE'")
    List<Subscription> findWithAutoRenewEnabled();
    
    /**
     * Check if subscription exists for tenant
     * @param tenantId The tenant ID
     * @return True if exists, false otherwise
     */
    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.tenantId.value = :tenantId")
    boolean existsByTenantIdValue(@Param("tenantId") Long tenantId);
    
    /**
     * Count subscriptions by plan ID
     * @param planId The plan ID
     * @return Number of subscriptions for the plan
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.planId.value = :planId")
    long countByPlanIdValue(@Param("planId") Long planId);
    
    /**
     * Count active subscriptions
     * @return Number of active subscriptions
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE'")
    long countByStatusActive();
} 