package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface WorkOrderMetricsRepository extends JpaRepository<WorkOrder, Long> {
    
    // ========== MÉTRICAS BÁSICAS ==========
    
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.start BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId")
    Long countCompletedInPeriod(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(CAST(FUNCTION('TIMESTAMPDIFF', HOUR, wo.executionWindow.start, wo.executionWindow.end) AS double)) FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL AND wo.tenantId.value = :tenantId")
    Double getAverageCompletionTimeInHours(@Param("tenantId") String tenantId);
    
    @Query("SELECT (COUNT(wo) * 100.0 / (SELECT COUNT(wo2) FROM WorkOrder wo2 WHERE wo2.tenantId.value = :tenantId AND wo2.createdAt >= :startDate)) FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.tenantId.value = :tenantId AND wo.createdAt >= :startDate")
    Double getCompletionRateLastMonth(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate);
    
    // ========== MÉTRICAS POR MÁQUINA ==========
    
    @Query("SELECT wo.machineId.value, COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId GROUP BY wo.machineId.value ORDER BY COUNT(wo) DESC")
    List<Object[]> getTopMachinesWithMostOrders(@Param("tenantId") String tenantId, @Param("limit") int limit);
    
    @Query("SELECT wo.machineId.value, COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId AND wo.status = 'COMPLETED' GROUP BY wo.machineId.value ORDER BY COUNT(wo) DESC")
    List<Object[]> getHighRiskMachines(@Param("tenantId") String tenantId);
    
    // ========== MÉTRICAS POR PRIORIDAD Y ESTADO ==========
    
    @Query("SELECT wo.status, COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId GROUP BY wo.status")
    List<Object[]> getOrdersByPriority(@Param("tenantId") String tenantId);
    
    @Query("SELECT wo.status, AVG(CAST(FUNCTION('TIMESTAMPDIFF', HOUR, wo.createdAt, wo.executionWindow.start) AS double)) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId AND wo.executionWindow.start IS NOT NULL GROUP BY wo.status")
    List<Object[]> getAverageTimeByStatus(@Param("tenantId") String tenantId);
    
    // ========== MÉTRICAS DE RESOLUCIÓN ==========
    
    @Query("SELECT AVG(CAST(FUNCTION('TIMESTAMPDIFF', HOUR, wo.createdAt, wo.executionWindow.end) AS double)) FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL AND wo.tenantId.value = :tenantId")
    Double getAverageResolutionTime(@Param("tenantId") String tenantId);
    
    @Query("SELECT (COUNT(wo) * 100.0 / (SELECT COUNT(wo2) FROM WorkOrder wo2 WHERE wo2.status = 'COMPLETED' AND wo2.tenantId.value = :tenantId)) FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.tenantId.value = :tenantId AND SIZE(wo.comments) = 0")
    Double getFirstTimeFixRate(@Param("tenantId") String tenantId);
    
    // ========== MÉTRICAS DE COSTOS ==========
    
    @Query("SELECT AVG(COALESCE(SUM(wm.requestedQty * 10.0), 0)) FROM WorkOrder wo LEFT JOIN wo.materials wm WHERE wo.status = 'COMPLETED' AND wo.tenantId.value = :tenantId GROUP BY wo.id")
    Double getAverageCostPerOrder(@Param("tenantId") String tenantId);
    
    @Query("SELECT COALESCE(SUM(wm.requestedQty * 10.0), 0) FROM WorkOrder wo LEFT JOIN wo.materials wm WHERE wo.status = 'COMPLETED' AND wo.executionWindow.start BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId")
    Double getTotalCostInPeriod(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ========== MÉTRICAS DE MATERIALES ==========
    
    @Query("SELECT wm.itemId.value, wm.itemName, SUM(wm.requestedQty) FROM WorkOrder wo JOIN wo.materials wm WHERE wo.executionWindow.start BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId GROUP BY wm.itemId.value, wm.itemName ORDER BY SUM(wm.requestedQty) DESC")
    List<Object[]> getMostUsedMaterials(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("tenantId") Long tenantId);
    
    // ========== MÉTRICAS TEMPORALES ==========
    
    @Query("SELECT DATE(wo.createdAt), COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId AND wo.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(wo.createdAt) ORDER BY DATE(wo.createdAt)")
    List<Object[]> getMonthlyTrends(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(CAST(FUNCTION('TIMESTAMPDIFF', HOUR, wo.executionWindow.start, wo.executionWindow.end) AS double)) FROM WorkOrder wo WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL AND wo.executionWindow.start BETWEEN :startDate AND :endDate AND wo.tenantId.value = :tenantId")
    Double getAverageCompletionTimeInPeriod(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ========== MÉTRICAS DE CATEGORÍAS ==========
    
    @Query("SELECT wo.status, COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId AND wo.createdAt BETWEEN :startDate AND :endDate GROUP BY wo.status")
    List<Object[]> getOrdersByCategoryInPeriod(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ========== MÉTRICAS PREDICTIVAS ==========
    
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId AND wo.createdAt >= :startDate")
    Long getWorkloadForecast(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT 'Preventive maintenance recommended' FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId GROUP BY wo.machineId.value HAVING COUNT(wo) > 5")
    List<String> getPreventiveMaintenanceRecommendations(@Param("tenantId") String tenantId);
    
    @Query("SELECT wo.machineId.value, COUNT(wo) FROM WorkOrder wo WHERE wo.tenantId.value = :tenantId GROUP BY wo.machineId.value HAVING COUNT(wo) > 3")
    List<Object[]> getFailurePatterns(@Param("tenantId") String tenantId);
} 