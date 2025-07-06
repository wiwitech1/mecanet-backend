package com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.ProductionLine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ProductionLineStatus;

/**
 * Production Line repository interface for JPA operations
 * Provides multi-tenant aware database operations for ProductionLine aggregate
 */
@Repository
public interface ProductionLineRepository extends JpaRepository<ProductionLine, Long> {
    
    /**
     * Find production line by ID and tenant ID
     * @param id The production line ID
     * @param tenantId The tenant ID
     * @return Optional production line if found
     */
    @Query("SELECT pl FROM ProductionLine pl WHERE pl.id = :id AND pl.tenantId.value = :tenantId")
    Optional<ProductionLine> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * Find production lines by plant ID and tenant ID
     * @param plantId The plant ID
     * @param tenantId The tenant ID
     * @return List of production lines for the plant
     */
    @Query("SELECT pl FROM ProductionLine pl WHERE pl.plantId = :plantId AND pl.tenantId.value = :tenantId")
    List<ProductionLine> findByPlantIdAndTenantId(@Param("plantId") Long plantId, @Param("tenantId") Long tenantId);
    
    /**
     * Find running production lines by tenant ID
     * @param tenantId The tenant ID
     * @return List of running production lines
     */
    @Query("SELECT pl FROM ProductionLine pl WHERE pl.tenantId.value = :tenantId AND pl.status = 'RUNNING'")
    List<ProductionLine> findRunningByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Find production lines by status and tenant ID
     * @param status The production line status
     * @param tenantId The tenant ID
     * @return List of production lines with the specified status
     */
    @Query("SELECT pl FROM ProductionLine pl WHERE pl.status = :status AND pl.tenantId.value = :tenantId")
    List<ProductionLine> findByStatusAndTenantId(@Param("status") ProductionLineStatus status, @Param("tenantId") Long tenantId);
    
    /**
     * Check if production line exists by name, plant ID and tenant ID
     * @param name The production line name
     * @param plantId The plant ID
     * @param tenantId The tenant ID
     * @return True if exists, false otherwise
     */
    @Query("SELECT COUNT(pl) > 0 FROM ProductionLine pl WHERE pl.name = :name AND pl.plantId = :plantId AND pl.tenantId.value = :tenantId")
    boolean existsByNameAndPlantIdAndTenantId(@Param("name") String name, @Param("plantId") Long plantId, @Param("tenantId") Long tenantId);
    
    /**
     * Count machines assigned to a production line
     * @param productionLineId The production line ID
     * @param tenantId The tenant ID
     * @return Number of machines assigned
     */
    @Query("SELECT COUNT(m) FROM Machine m WHERE m.productionLineId = :productionLineId AND m.tenantId.value = :tenantId")
    long countMachinesByProductionLineIdAndTenantId(@Param("productionLineId") Long productionLineId, @Param("tenantId") Long tenantId);
}