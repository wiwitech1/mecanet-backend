package com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.inventory.domain.model.aggregates.InventoryItem;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemCategory;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de InventoryItem
 * Proporciona operaciones de base de datos multitenancy para el agregado InventoryItem
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    /**
     * Buscar todos los items por tenant ID con paginación
     * @param tenantId ID del tenant
     * @param pageable Configuración de paginación
     * @return Página de items del tenant
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.tenantId.value = :tenantId")
    Page<InventoryItem> findByTenantIdValue(@Param("tenantId") Long tenantId, Pageable pageable);
    
    /**
     * Buscar todos los items por tenant ID
     * @param tenantId ID del tenant
     * @return Lista de items del tenant
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.tenantId.value = :tenantId")
    List<InventoryItem> findByTenantIdValue(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar item por ID y tenant ID
     * @param id ID del item
     * @param tenantId ID del tenant
     * @return Optional item si se encuentra
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.id = :id AND i.tenantId.value = :tenantId")
    Optional<InventoryItem> findByIdAndTenantIdValue(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * Verificar si existe item por SKU y tenant ID
     * @param sku SKU del item
     * @param tenantId ID del tenant
     * @return true si existe, false en caso contrario
     */
    @Query("SELECT COUNT(i) > 0 FROM InventoryItem i WHERE i.sku = :sku AND i.tenantId.value = :tenantId")
    boolean existsBySkuAndTenantIdValue(@Param("sku") String sku, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar items por planta y tenant ID
     * @param plantId ID de la planta
     * @param tenantId ID del tenant
     * @return Lista de items de la planta
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.plantId = :plantId AND i.tenantId.value = :tenantId")
    List<InventoryItem> findByPlantIdAndTenantIdValue(@Param("plantId") Long plantId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar items con stock bajo por tenant ID
     * @param tenantId ID del tenant
     * @return Lista de items con stock bajo
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.tenantId.value = :tenantId AND i.currentStock <= i.minimumStock")
    List<InventoryItem> findLowStockItemsByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar items por categoría y tenant ID
     * @param category Categoría del item
     * @param tenantId ID del tenant
     * @return Lista de items de la categoría
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.category = :category AND i.tenantId.value = :tenantId")
    List<InventoryItem> findByCategoryAndTenantIdValue(@Param("category") ItemCategory category, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar items por estado y tenant ID
     * @param status Estado del item
     * @param tenantId ID del tenant
     * @return Lista de items con el estado especificado
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.status = :status AND i.tenantId.value = :tenantId")
    List<InventoryItem> findByStatusAndTenantIdValue(@Param("status") ItemStatus status, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar items vencidos por tenant ID
     * @param tenantId ID del tenant
     * @return Lista de items vencidos
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.tenantId.value = :tenantId AND i.expirationDate IS NOT NULL AND i.expirationDate < :today")
    List<InventoryItem> findExpiredItemsByTenantId(@Param("tenantId") Long tenantId, @Param("today") LocalDate today);
    
    /**
     * Buscar items por SKU y tenant ID
     * @param sku SKU del item
     * @param tenantId ID del tenant
     * @return Optional item si se encuentra
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.sku = :sku AND i.tenantId.value = :tenantId")
    Optional<InventoryItem> findBySkuAndTenantIdValue(@Param("sku") String sku, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar items compatibles con una máquina por tenant ID
     * @param machineId ID de la máquina
     * @param tenantId ID del tenant
     * @return Lista de items compatibles con la máquina
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.tenantId.value = :tenantId AND i.compatibleMachines LIKE %:machineId%")
    List<InventoryItem> findByCompatibleMachineIdAndTenantIdValue(@Param("machineId") String machineId, @Param("tenantId") Long tenantId);
    
    /**
     * Contar items por planta y tenant ID
     * @param plantId ID de la planta
     * @param tenantId ID del tenant
     * @return Número de items en la planta
     */
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.plantId = :plantId AND i.tenantId.value = :tenantId")
    long countByPlantIdAndTenantIdValue(@Param("plantId") Long plantId, @Param("tenantId") Long tenantId);
    
    /**
     * Contar items con stock bajo por tenant ID
     * @param tenantId ID del tenant
     * @return Número de items con stock bajo
     */
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.tenantId.value = :tenantId AND i.currentStock <= i.minimumStock")
    long countLowStockItemsByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * Contar items por tenant ID
     * @param tenantId ID del tenant
     * @return Número total de items del tenant
     */
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.tenantId.value = :tenantId")
    long countByTenantIdValue(@Param("tenantId") Long tenantId);
    
    /**
     * Contar items por estado y tenant ID
     * @param status Estado del item
     * @param tenantId ID del tenant
     * @return Número de items con el estado especificado
     */
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.status = :status AND i.tenantId.value = :tenantId")
    long countByStatusAndTenantIdValue(@Param("status") com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ItemStatus status, @Param("tenantId") Long tenantId);
} 