package com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Plant;

/**
 * Plant repository interface for JPA operations
 * Provides multi-tenant aware database operations for Plant aggregate
 */
@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    
    /**
     * Find all plants by tenant ID
     * @param tenantId The tenant ID
     * @return List of plants for the tenant
     */
    @Query("SELECT p FROM Plant p WHERE p.tenantId.value = :tenantId")
    List<Plant> findAllByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Find active plants by tenant ID
     * @param tenantId The tenant ID
     * @return List of active plants for the tenant
     */
    @Query("SELECT p FROM Plant p WHERE p.tenantId.value = :tenantId AND p.active = true")
    List<Plant> findAllByTenantIdAndIsActiveTrue(@Param("tenantId") Long tenantId);
    
    /**
     * Find plant by ID and tenant ID
     * @param id The plant ID
     * @param tenantId The tenant ID
     * @return Optional plant if found
     */
    @Query("SELECT p FROM Plant p WHERE p.id = :id AND p.tenantId.value = :tenantId")
    Optional<Plant> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * Check if plant exists by name and tenant ID
     * @param name The plant name
     * @param tenantId The tenant ID
     * @return True if exists, false otherwise
     */
    @Query("SELECT COUNT(p) > 0 FROM Plant p WHERE p.name = :name AND p.tenantId.value = :tenantId")
    boolean existsByNameAndTenantId(@Param("name") String name, @Param("tenantId") Long tenantId);
    
    /**
     * Count active production lines for a plant
     * @param plantId The plant ID
     * @param tenantId The tenant ID
     * @return Number of active production lines
     */
    @Query("SELECT COUNT(pl) FROM ProductionLine pl WHERE pl.plantId = :plantId AND pl.tenantId.value = :tenantId AND pl.status = 'RUNNING'")
    long countActiveProductionLinesByPlantIdAndTenantId(@Param("plantId") Long plantId, @Param("tenantId") Long tenantId);
}