package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    
    // ========== QUERIES BÁSICAS EXISTENTES ==========
    Optional<WorkOrder> findByWorkOrderIdValueAndTenantIdValue(Long workOrderId, Long tenantId);
    List<WorkOrder> findByStatusAndTenantIdValue(WorkOrderStatus status, Long tenantId);
    boolean existsByWorkOrderIdValueAndTenantIdValue(Long workOrderId, Long tenantId);
    long countByStatusAndTenantIdValue(WorkOrderStatus status, Long tenantId);
    List<WorkOrder> findByMachineIdValueAndTenantIdValue(Long machineId, Long tenantId);
    
    @Query("SELECT DISTINCT wo FROM WorkOrder wo JOIN wo.technicians wt WHERE wt.technicianId = :technicianId AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByTechnicianAndTenantId(@Param("technicianId") TechnicianId technicianId, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA FASE PUBLISHED (Técnicos se unen/salen) ==========
    
    /**
     * Buscar work orders disponibles para que técnicos se unan
     * (PUBLISHED y con espacio disponible)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = :status AND wo.tenantId.value = :tenantId AND SIZE(wo.technicians) < wo.maxTechnicians")
    List<WorkOrder> findAvailableWorkOrders(@Param("status") WorkOrderStatus status, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders donde un técnico específico puede unirse
     * (PUBLISHED, con espacio, y que no esté ya inscrito)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'PUBLISHED' AND wo.tenantId.value = :tenantId " +
           "AND SIZE(wo.technicians) < wo.maxTechnicians " +
           "AND NOT EXISTS (SELECT 1 FROM wo.technicians wt WHERE wt.technicianId = :technicianId AND wt.status = 'JOINED')")
    List<WorkOrder> findAvailableForTechnician(@Param("technicianId") TechnicianId technicianId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders por skills requeridas (para asignación inteligente)
     */
    @Query("SELECT DISTINCT wo FROM WorkOrder wo JOIN wo.requiredSkillIds skill WHERE skill IN :skillIds AND wo.status = 'PUBLISHED' AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByRequiredSkillsAndStatus(@Param("skillIds") List<Long> skillIds, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA PLANNING Y SCHEDULING ==========
    
    /**
     * Buscar work orders por rango de fechas programadas
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.schedule.date BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByScheduleDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders programados para una fecha específica
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.schedule.date = :date AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByScheduleDate(@Param("date") LocalDate date, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders próximos a ejecutar (para dashboard admin)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status IN ('PUBLISHED', 'REVIEW', 'PENDING_EXECUTION') " +
           "AND wo.schedule.date BETWEEN :fromDate AND :toDate AND wo.tenantId.value = :tenantId " +
           "ORDER BY wo.schedule.date ASC, wo.schedule.startTime ASC")
    List<WorkOrder> findUpcomingWorkOrders(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA DASHBOARD ADMIN ==========
    
    /**
     * Buscar work orders por múltiples estados y fecha
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status IN :statuses AND wo.schedule.date = :date AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByStatusesAndScheduleDate(@Param("statuses") List<WorkOrderStatus> statuses, @Param("date") LocalDate date, @Param("tenantId") Long tenantId);
    
    /**
     * Contar work orders por estado en un rango de fechas
     */
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.status = :status AND wo.schedule.date BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId")
    long countByStatusAndDateRange(@Param("status") WorkOrderStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders que necesitan revisión (en estado PUBLISHED por mucho tiempo)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'PUBLISHED' AND wo.createdAt < :cutoffDate AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findRequiringReview(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA TRACKING DE PLANES DINÁMICOS ==========
    
    /**
     * Buscar work orders por plan dinámico
     */
    List<WorkOrder> findByPlanIdValueAndTenantIdValue(Long planId, Long tenantId);
    
    /**
     * Buscar work orders por tarea específica de un plan
     */
    List<WorkOrder> findByTaskIdValueAndTenantIdValue(Long taskId, Long tenantId);
    
    /**
     * Contar work orders creados desde un plan específico
     */
    long countByPlanIdValueAndTenantIdValue(Long planId, Long tenantId);
    
    // ========== QUERIES PARA GESTIÓN DE MATERIALES ==========
    
    /**
     * Buscar work orders que usan un material específico
     */
    @Query("SELECT DISTINCT wo FROM WorkOrder wo JOIN wo.materials wm WHERE wm.itemId.value = :itemId AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByMaterialItemId(@Param("itemId") Long itemId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders con materiales pendientes de confirmar
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'REVIEW' AND SIZE(wo.materials) > 0 AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findRequiringMaterialReview(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders con discrepancias en materiales (requestedQty vs finalQty)
     */
    @Query("SELECT DISTINCT wo FROM WorkOrder wo JOIN wo.materials wm WHERE wo.status = 'COMPLETED' " +
           "AND wm.finalQty IS NOT NULL AND wm.requestedQty != wm.finalQty AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findWithMaterialDiscrepancies(@Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA REPORTES Y AUDITORÍA ==========
    
    /**
     * Buscar work orders completados en un rango de fechas
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.start BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findCompletedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders por duración de ejecución (para análisis de eficiencia)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL " +
           "AND FUNCTION('TIMESTAMPDIFF', HOUR, wo.executionWindow.start, wo.executionWindow.end) > :minHours AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByExecutionDurationGreaterThan(@Param("minHours") int minHours, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders con comentarios (para auditoría)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE SIZE(wo.comments) > 0 AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findWithComments(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders con fotos (para auditoría)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE SIZE(wo.photos) > 0 AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findWithPhotos(@Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA MÉTRICAS Y ESTADÍSTICAS ==========
    
    /**
     * Obtener duración promedio de ejecución por máquina
     */
    @Query("SELECT wo.machineId.value, AVG(CAST(FUNCTION('TIMESTAMPDIFF', MINUTE, wo.executionWindow.start, wo.executionWindow.end) AS double)) " +
           "FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL AND wo.tenantId.value = :tenantId " +
           "GROUP BY wo.machineId.value")
    List<Object[]> getExecutionTimeStatisticsByMachine(@Param("tenantId") Long tenantId);
    
    /**
     * Contar work orders por técnico (para métricas de productividad)
     */
    @Query("SELECT wt.technicianId, COUNT(wo) FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wo.status = 'COMPLETED' AND wo.tenantId.value = :tenantId " +
           "GROUP BY wt.technicianId")
    List<Object[]> countCompletedWorkOrdersByTechnician(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar work orders con retrasos (ejecutados después de la fecha programada)
     */
    @Query("SELECT wo FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.start IS NOT NULL " +
           "AND DATE(wo.executionWindow.start) > wo.schedule.date AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findDelayedWorkOrders(@Param("tenantId") Long tenantId);
} 