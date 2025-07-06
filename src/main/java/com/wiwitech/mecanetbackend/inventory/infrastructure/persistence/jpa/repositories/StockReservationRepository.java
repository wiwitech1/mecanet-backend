package com.wiwitech.mecanetbackend.inventory.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.inventory.domain.model.entities.StockReservation;
import com.wiwitech.mecanetbackend.inventory.domain.model.valueobjects.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para operaciones de StockReservation
 * Proporciona operaciones de base de datos multitenancy para la entidad StockReservation
 */
@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    
    /**
     * Buscar reservas por item de inventario y tenant ID
     * @param inventoryItemId ID del item de inventario
     * @param tenantId ID del tenant
     * @return Lista de reservas del item
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.inventoryItemId = :inventoryItemId AND sr.tenantId = :tenantId ORDER BY sr.reservationDate DESC")
    List<StockReservation> findByInventoryItemIdAndTenantId(@Param("inventoryItemId") Long inventoryItemId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar reservas activas por item de inventario y tenant ID
     * @param inventoryItemId ID del item de inventario
     * @param tenantId ID del tenant
     * @return Lista de reservas activas del item
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.inventoryItemId = :inventoryItemId AND sr.tenantId = :tenantId AND sr.status = 'ACTIVE' ORDER BY sr.reservationDate DESC")
    List<StockReservation> findActiveByInventoryItemIdAndTenantId(@Param("inventoryItemId") Long inventoryItemId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar reservas por usuario y tenant ID
     * @param userId ID del usuario
     * @param tenantId ID del tenant
     * @return Lista de reservas del usuario
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.userId = :userId AND sr.tenantId = :tenantId ORDER BY sr.reservationDate DESC")
    List<StockReservation> findByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar reservas por estado y tenant ID
     * @param status Estado de la reserva
     * @param tenantId ID del tenant
     * @return Lista de reservas con el estado especificado
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.status = :status AND sr.tenantId = :tenantId ORDER BY sr.reservationDate DESC")
    List<StockReservation> findByStatusAndTenantId(@Param("status") ReservationStatus status, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar reservas por tipo de reserva y tenant ID
     * @param reservationType Tipo de reserva
     * @param tenantId ID del tenant
     * @return Lista de reservas del tipo especificado
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.reservationType = :reservationType AND sr.tenantId = :tenantId ORDER BY sr.reservationDate DESC")
    List<StockReservation> findByReservationTypeAndTenantId(@Param("reservationType") String reservationType, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar reservas vencidas por tenant ID
     * @param tenantId ID del tenant
     * @return Lista de reservas vencidas
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.tenantId = :tenantId AND sr.expirationDate IS NOT NULL AND sr.expirationDate < :now AND sr.status = 'ACTIVE'")
    List<StockReservation> findExpiredReservationsByTenantId(@Param("tenantId") Long tenantId, @Param("now") LocalDateTime now);
    
    /**
     * Buscar reservas por rango de fechas y tenant ID
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param tenantId ID del tenant
     * @return Lista de reservas en el rango de fechas
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.reservationDate BETWEEN :startDate AND :endDate AND sr.tenantId = :tenantId ORDER BY sr.reservationDate DESC")
    List<StockReservation> findByReservationDateBetweenAndTenantId(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar reservas por tenant ID con paginación
     * @param tenantId ID del tenant
     * @param pageable Configuración de paginación
     * @return Página de reservas del tenant
     */
    @Query("SELECT sr FROM StockReservation sr WHERE sr.tenantId = :tenantId ORDER BY sr.reservationDate DESC")
    Page<StockReservation> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);
    
    /**
     * Contar reservas activas por item de inventario y tenant ID
     * @param inventoryItemId ID del item de inventario
     * @param tenantId ID del tenant
     * @return Número de reservas activas del item
     */
    @Query("SELECT COUNT(sr) FROM StockReservation sr WHERE sr.inventoryItemId = :inventoryItemId AND sr.tenantId = :tenantId AND sr.status = 'ACTIVE'")
    long countActiveByInventoryItemIdAndTenantId(@Param("inventoryItemId") Long inventoryItemId, @Param("tenantId") Long tenantId);
    
    /**
     * Contar reservas por usuario y tenant ID
     * @param userId ID del usuario
     * @param tenantId ID del tenant
     * @return Número de reservas del usuario
     */
    @Query("SELECT COUNT(sr) FROM StockReservation sr WHERE sr.userId = :userId AND sr.tenantId = :tenantId")
    long countByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
    
    /**
     * Contar reservas vencidas por tenant ID
     * @param tenantId ID del tenant
     * @return Número de reservas vencidas
     */
    @Query("SELECT COUNT(sr) FROM StockReservation sr WHERE sr.tenantId = :tenantId AND sr.expirationDate IS NOT NULL AND sr.expirationDate < :now AND sr.status = 'ACTIVE'")
    long countExpiredReservationsByTenantId(@Param("tenantId") Long tenantId, @Param("now") LocalDateTime now);
    
} 