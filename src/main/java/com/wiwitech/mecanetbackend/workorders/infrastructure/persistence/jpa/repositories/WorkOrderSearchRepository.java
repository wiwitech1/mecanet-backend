package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Repositorio especializado para búsquedas avanzadas y filtros complejos de Work Orders
 */
@Repository
public interface WorkOrderSearchRepository extends JpaRepository<WorkOrder, Long> {
    
    // ========== BÚSQUEDAS PARA TÉCNICOS ==========
    
    /**
     * Buscar work orders disponibles para técnicos (con paginación)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'PUBLISHED' AND wo.tenantId.value = :tenantId " +
           "AND SIZE(wo.technicians) < wo.maxTechnicians " +
           "AND NOT EXISTS (SELECT 1 FROM wo.technicians wt WHERE wt.technicianId = :technicianId AND wt.status = 'JOINED') " +
           "ORDER BY wo.schedule.date ASC, wo.schedule.startTime ASC")
    Page<WorkOrder> findAvailableWorkOrdersForTechnician(@Param("technicianId") TechnicianId technicianId, 
                                                        @Param("tenantId") Long tenantId, 
                                                        Pageable pageable);
    
    /**
     * Buscar work orders por skills compatibles con un técnico
     */
    @Query("SELECT DISTINCT wo FROM WorkOrder wo JOIN wo.requiredSkillIds skill " +
           "WHERE wo.status = 'PUBLISHED' AND wo.tenantId.value = :tenantId " +
           "AND skill IN :technicianSkills " +
           "AND SIZE(wo.technicians) < wo.maxTechnicians " +
           "AND NOT EXISTS (SELECT 1 FROM wo.technicians wt WHERE wt.technicianId = :technicianId AND wt.status = 'JOINED') " +
           "ORDER BY wo.schedule.date ASC")
    List<WorkOrder> findWorkOrdersByCompatibleSkills(@Param("technicianId") TechnicianId technicianId,
                                                    @Param("technicianSkills") Set<Long> technicianSkills,
                                                    @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders por turno compatible
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'PUBLISHED' AND wo.tenantId.value = :tenantId " +
           "AND SIZE(wo.technicians) < wo.maxTechnicians " +
           "AND ((:shift = 'DAY' AND HOUR(wo.schedule.startTime) BETWEEN 6 AND 18) " +
           "OR (:shift = 'NIGHT' AND (HOUR(wo.schedule.startTime) >= 18 OR HOUR(wo.schedule.startTime) <= 6))) " +
           "ORDER BY wo.schedule.date ASC, wo.schedule.startTime ASC")
    List<WorkOrder> findWorkOrdersByShiftCompatibility(@Param("shift") Shift shift, 
                                                      @Param("tenantId") Long tenantId);
    
    // ========== BÚSQUEDAS PARA ADMIN ==========
    
    /**
     * Búsqueda avanzada con múltiples filtros
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId " +
           "AND (:statuses IS NULL OR wo.status IN :statuses) " +
           "AND (:machineIds IS NULL OR wo.machineId.value IN :machineIds) " +
           "AND (:planIds IS NULL OR wo.planId.value IN :planIds) " +
           "AND (:dateFrom IS NULL OR wo.schedule.date >= :dateFrom) " +
           "AND (:dateTo IS NULL OR wo.schedule.date <= :dateTo) " +
           "AND (:hasComments IS NULL OR (:hasComments = true AND SIZE(wo.comments) > 0) OR (:hasComments = false AND SIZE(wo.comments) = 0)) " +
           "AND (:hasPhotos IS NULL OR (:hasPhotos = true AND SIZE(wo.photos) > 0) OR (:hasPhotos = false AND SIZE(wo.photos) = 0)) " +
           "ORDER BY wo.schedule.date DESC, wo.createdAt DESC")
    Page<WorkOrder> findByAdvancedFilters(@Param("statuses") Set<WorkOrderStatus> statuses,
                                        @Param("machineIds") Set<Long> machineIds,
                                        @Param("planIds") Set<Long> planIds,
                                        @Param("dateFrom") LocalDate dateFrom,
                                        @Param("dateTo") LocalDate dateTo,
                                        @Param("hasComments") Boolean hasComments,
                                        @Param("hasPhotos") Boolean hasPhotos,
                                        @Param("tenantId") Long tenantId,
                                        Pageable pageable);
    
    /**
     * Buscar work orders por texto libre (título, descripción, conclusiones)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId " +
           "AND (LOWER(wo.title) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(wo.description) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(wo.conclusions) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY wo.schedule.date DESC")
    Page<WorkOrder> findByTextSearch(@Param("searchText") String searchText, 
                                   @Param("tenantId") Long tenantId, 
                                   Pageable pageable);
    
    // ========== BÚSQUEDAS PARA DASHBOARD ==========
    
    /**
     * Obtener work orders críticos (próximos a vencer, con retrasos, etc.)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId " +
           "AND ((wo.status IN ('PUBLISHED', 'REVIEW') AND wo.schedule.date <= :criticalDate) " +
           "OR (wo.status = 'PENDING_EXECUTION' AND wo.schedule.date < CURRENT_DATE) " +
           "OR (wo.status = 'PUBLISHED' AND SIZE(wo.technicians) = 0)) " +
           "ORDER BY wo.schedule.date ASC")
    List<WorkOrder> findCriticalWorkOrders(@Param("criticalDate") LocalDate criticalDate, 
                                         @Param("tenantId") Long tenantId);
    
    /**
     * Obtener work orders por prioridad (basado en plan dinámico, máquina crítica, etc.)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId " +
           "AND wo.status IN ('NEW', 'PUBLISHED', 'REVIEW', 'PENDING_EXECUTION') " +
           "ORDER BY " +
           "CASE WHEN wo.planId.value IS NOT NULL THEN 1 ELSE 2 END, " +
           "wo.schedule.date ASC, " +
           "wo.createdAt ASC")
    List<WorkOrder> findWorkOrdersByPriority(@Param("tenantId") Long tenantId);
    
    // ========== BÚSQUEDAS PARA PLANIFICACIÓN ==========
    
    /**
     * Buscar work orders con conflictos de recursos (mismo técnico, misma fecha/hora)
     */
    @Query("SELECT wo FROM WorkOrder wo JOIN wo.technicians wt1 " +
           "WHERE wo.tenantId.value = :tenantId " +
           "AND wo.status IN ('PUBLISHED', 'REVIEW', 'PENDING_EXECUTION') " +
           "AND EXISTS (SELECT 1 FROM WorkOrder wo2 JOIN wo2.technicians wt2 " +
           "WHERE wo2.tenantId.value = :tenantId " +
           "AND wo2.id != wo.id " +
           "AND wo2.status IN ('PUBLISHED', 'REVIEW', 'PENDING_EXECUTION') " +
           "AND wt1.technicianId = wt2.technicianId " +
           "AND wo.schedule.date = wo2.schedule.date " +
           "AND wt1.status = 'JOINED' AND wt2.status = 'JOINED') " +
           "ORDER BY wo.schedule.date ASC")
    List<WorkOrder> findWorkOrdersWithResourceConflicts(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders optimizables (mismo día, misma máquina o área)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId " +
           "AND wo.status IN ('NEW', 'PUBLISHED', 'REVIEW') " +
           "AND EXISTS (SELECT 1 FROM WorkOrder wo2 " +
           "WHERE wo2.tenantId.value = :tenantId " +
           "AND wo2.id != wo.id " +
           "AND wo2.status IN ('NEW', 'PUBLISHED', 'REVIEW') " +
           "AND wo.schedule.date = wo2.schedule.date " +
           "AND wo.machineId.value = wo2.machineId.value) " +
           "ORDER BY wo.schedule.date ASC, wo.machineId.value ASC")
    List<WorkOrder> findOptimizableWorkOrders(@Param("tenantId") Long tenantId);
    
    // ========== BÚSQUEDAS PARA REPORTES ==========
    
    /**
     * Buscar work orders para reporte de eficiencia
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'COMPLETED' " +
           "AND wo.executionWindow.start IS NOT NULL AND wo.executionWindow.end IS NOT NULL " +
           "AND wo.schedule.date BETWEEN :startDate AND :endDate " +
           "AND wo.tenantId.value = :tenantId " +
           "ORDER BY wo.schedule.date ASC")
    List<WorkOrder> findForEfficiencyReport(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate, 
                                          @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders para reporte de materiales
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'COMPLETED' " +
           "AND SIZE(wo.materials) > 0 " +
           "AND wo.schedule.date BETWEEN :startDate AND :endDate " +
           "AND wo.tenantId.value = :tenantId " +
           "ORDER BY wo.schedule.date ASC")
    List<WorkOrder> findForMaterialReport(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate, 
                                        @Param("tenantId") Long tenantId);
    
    // ========== BÚSQUEDAS PARA AUDITORÍA ==========
    
    /**
     * Buscar work orders con anomalías
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId " +
           "AND ((wo.status = 'COMPLETED' AND wo.executionWindow.start IS NULL) " +
           "OR (wo.status = 'COMPLETED' AND wo.executionWindow.end IS NULL) " +
           "OR (wo.status = 'COMPLETED' AND wo.conclusions IS NULL) " +
           "OR (wo.status = 'IN_EXECUTION' AND wo.createdAt < :oldDate) " +
           "OR (wo.status = 'PUBLISHED' AND SIZE(wo.technicians) = 0 AND wo.createdAt < :oldDate)) " +
           "ORDER BY wo.createdAt DESC")
    List<WorkOrder> findAnomalousWorkOrders(@Param("oldDate") LocalDateTime oldDate, 
                                          @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders para auditoría de calidad
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'COMPLETED' " +
           "AND wo.tenantId.value = :tenantId " +
           "AND (SIZE(wo.comments) > :minComments " +
           "OR SIZE(wo.photos) > :minPhotos " +
           "OR FUNCTION('TIMESTAMPDIFF', HOUR, wo.executionWindow.start, wo.executionWindow.end) > :minHours) " +
           "ORDER BY wo.schedule.date DESC")
    List<WorkOrder> findForQualityAudit(@Param("minComments") int minComments,
                                      @Param("minPhotos") int minPhotos,
                                      @Param("minHours") int minHours,
                                      @Param("tenantId") Long tenantId);
    
    // ========== BÚSQUEDAS PARA MOBILE/TÉCNICOS ==========
    
    /**
     * Buscar work orders activos para un técnico específico
     */
    @Query("SELECT wo FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wt.technicianId = :technicianId AND wt.status = 'JOINED' " +
           "AND wo.status IN ('PUBLISHED', 'REVIEW', 'PENDING_EXECUTION', 'IN_EXECUTION') " +
           "AND wo.tenantId.value = :tenantId " +
           "ORDER BY " +
           "CASE wo.status " +
           "WHEN 'IN_EXECUTION' THEN 1 " +
           "WHEN 'PENDING_EXECUTION' THEN 2 " +
           "WHEN 'REVIEW' THEN 3 " +
           "WHEN 'PUBLISHED' THEN 4 " +
           "END, wo.schedule.date ASC")
    List<WorkOrder> findActiveWorkOrdersForTechnician(@Param("technicianId") TechnicianId technicianId, 
                                                     @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders del día para un técnico
     */
    @Query("SELECT wo FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wt.technicianId = :technicianId AND wt.status = 'JOINED' " +
           "AND wo.schedule.date = :date AND wo.tenantId.value = :tenantId " +
           "ORDER BY wo.schedule.startTime ASC")
    List<WorkOrder> findDailyWorkOrdersForTechnician(@Param("technicianId") TechnicianId technicianId, 
                                                    @Param("date") LocalDate date, 
                                                    @Param("tenantId") Long tenantId);
} 