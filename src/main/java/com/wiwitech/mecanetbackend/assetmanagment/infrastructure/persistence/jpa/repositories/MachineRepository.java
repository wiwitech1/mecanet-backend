package com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Machine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MachineStatus;

/**
 * Machine repository interface for JPA operations
 * Provides multi-tenant aware database operations for Machine aggregate
 */
@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    
    /**
     * Find all machines by tenant ID
     * @param tenantId The tenant ID
     * @return List of machines for the tenant
     */
    @Query("SELECT m FROM Machine m WHERE m.tenantId.value = :tenantId")
    List<Machine> findAllByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Find machine by ID and tenant ID
     * @param id The machine ID
     * @param tenantId The tenant ID
     * @return Optional machine if found
     */
    @Query("SELECT m FROM Machine m WHERE m.id = :id AND m.tenantId.value = :tenantId")
    Optional<Machine> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * Find machine by serial number and tenant ID
     * @param serialNumber The machine serial number
     * @param tenantId The tenant ID
     * @return Optional machine if found
     */
    @Query("SELECT m FROM Machine m WHERE m.serialNumber = :serialNumber AND m.tenantId.value = :tenantId")
    Optional<Machine> findBySerialNumberAndTenantId(@Param("serialNumber") String serialNumber, @Param("tenantId") Long tenantId);
    
    /**
     * Check if machine exists by serial number and tenant ID
     * @param serialNumber The machine serial number
     * @param tenantId The tenant ID
     * @return True if exists, false otherwise
     */
    @Query("SELECT COUNT(m) > 0 FROM Machine m WHERE m.serialNumber = :serialNumber AND m.tenantId.value = :tenantId")
    boolean existsBySerialNumberAndTenantId(@Param("serialNumber") String serialNumber, @Param("tenantId") Long tenantId);
    
    /**
     * Find available machines (operational and not assigned) by tenant ID
     * @param tenantId The tenant ID
     * @return List of available machines
     */
    @Query("SELECT m FROM Machine m WHERE m.tenantId.value = :tenantId AND m.status = 'OPERATIONAL' AND m.productionLineId IS NULL")
    List<Machine> findAvailableByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Find machines by production line ID and tenant ID
     * @param productionLineId The production line ID
     * @param tenantId The tenant ID
     * @return List of machines assigned to the production line
     */
    @Query("SELECT m FROM Machine m WHERE m.productionLineId = :productionLineId AND m.tenantId.value = :tenantId")
    List<Machine> findByProductionLineIdAndTenantId(@Param("productionLineId") Long productionLineId, @Param("tenantId") Long tenantId);
    
    /**
     * Find machines due for maintenance by tenant ID
     * @param tenantId The tenant ID
     * @return List of machines due for maintenance
     */
    @Query("SELECT m FROM Machine m WHERE m.tenantId.value = :tenantId AND m.maintenanceInfo.nextMaintenanceDate <= CURRENT_TIMESTAMP")
    List<Machine> findMachinesDueForMaintenanceByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Find machines by status and tenant ID
     * @param status The machine status
     * @param tenantId The tenant ID
     * @return List of machines with the specified status
     */
    @Query("SELECT m FROM Machine m WHERE m.status = :status AND m.tenantId.value = :tenantId")
    List<Machine> findByStatusAndTenantId(@Param("status") MachineStatus status, @Param("tenantId") Long tenantId);
}