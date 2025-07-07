package com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.inventory.domain.model.entities.StockMovement;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.StockOperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para la entidad StockMovement
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    /**
     * Buscar movimientos por item de inventario y tenant
     */
    List<StockMovement> findByInventoryItemIdAndTenantIdOrderByMovementDateDesc(Long inventoryItemId, Long tenantId);

    /**
     * Buscar movimientos por item de inventario y tenant con paginación
     */
    Page<StockMovement> findByInventoryItemIdAndTenantIdOrderByMovementDateDesc(Long inventoryItemId, Long tenantId, Pageable pageable);

    /**
     * Buscar movimientos por SKU y tenant
     */
    List<StockMovement> findBySkuAndTenantIdOrderByMovementDateDesc(String sku, Long tenantId);

    /**
     * Buscar movimientos por tipo de operación y tenant
     */
    List<StockMovement> findByOperationTypeAndTenantIdOrderByMovementDateDesc(StockOperationType operationType, Long tenantId);

    /**
     * Buscar movimientos por planta y tenant
     */
    List<StockMovement> findByPlantIdAndTenantIdOrderByMovementDateDesc(Long plantId, Long tenantId);

    /**
     * Buscar movimientos por usuario y tenant
     */
    List<StockMovement> findByUserIdAndTenantIdOrderByMovementDateDesc(Long userId, Long tenantId);

    /**
     * Buscar movimientos por máquina y tenant
     */
    List<StockMovement> findByMachineIdAndTenantIdOrderByMovementDateDesc(Long machineId, Long tenantId);

    /**
     * Buscar movimientos en un rango de fechas para un tenant
     */
    @Query("SELECT sm FROM StockMovement sm WHERE sm.tenantId = :tenantId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sm.movementDate DESC")
    List<StockMovement> findByTenantIdAndMovementDateBetween(
        @Param("tenantId") Long tenantId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Buscar movimientos por item y rango de fechas
     */
    @Query("SELECT sm FROM StockMovement sm WHERE sm.inventoryItemId = :inventoryItemId " +
           "AND sm.tenantId = :tenantId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sm.movementDate DESC")
    List<StockMovement> findByInventoryItemIdAndTenantIdAndMovementDateBetween(
        @Param("inventoryItemId") Long inventoryItemId,
        @Param("tenantId") Long tenantId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Contar movimientos por tenant
     */
    long countByTenantId(Long tenantId);

    /**
     * Contar movimientos por item y tenant
     */
    long countByInventoryItemIdAndTenantId(Long inventoryItemId, Long tenantId);

    /**
     * Buscar movimientos por tenant con paginación ordenados por fecha de creación descendente
     */
    Page<StockMovement> findByTenantIdOrderByCreatedAtDesc(Long tenantId, Pageable pageable);
} 