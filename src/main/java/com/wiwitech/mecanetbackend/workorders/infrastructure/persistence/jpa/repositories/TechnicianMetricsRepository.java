package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TechnicianMetricsRepository extends JpaRepository<Technician, Long> {
    
    // ========== MÉTRICAS DE ACTIVIDAD ==========
    
    @Query("SELECT t, COUNT(wo) FROM Technician t " +
           "JOIN WorkOrder wo ON wo.tenantId.value = t.tenantId.value " +
           "JOIN wo.technicians wt ON wt.technicianId.value = t.id " +
           "WHERE wo.status = 'COMPLETED' AND t.tenantId.value = :tenantId " +
           "GROUP BY t ORDER BY COUNT(wo) DESC")
    List<Object[]> getMostActiveTechnicians(@Param("tenantId") String tenantId, @Param("limit") int limit);
    
    @Query("SELECT COUNT(t) FROM Technician t WHERE t.currentStatus = 'AVAILABLE' AND t.tenantId.value = :tenantId")
    Long getAvailableTechniciansCount(@Param("tenantId") String tenantId);
    
    // ========== MÉTRICAS DE CARGA DE TRABAJO ==========
    
    @Query("SELECT t.currentStatus, COUNT(t) FROM Technician t WHERE t.tenantId.value = :tenantId GROUP BY t.currentStatus")
    List<Object[]> getTechnicianWorkloadDistribution(@Param("tenantId") String tenantId);
    
    @Query("SELECT (COUNT(DISTINCT wt.technicianId.value) * 100.0 / COUNT(DISTINCT t.id)) FROM Technician t " +
           "LEFT JOIN WorkOrder wo ON wo.tenantId.value = t.tenantId.value " +
           "LEFT JOIN wo.technicians wt ON wt.technicianId.value = t.id AND wo.status IN ('PENDING_EXECUTION', 'IN_EXECUTION') " +
           "WHERE t.tenantId.value = :tenantId")
    Double getTechnicianUtilizationRate(@Param("tenantId") String tenantId);
    
    // ========== MÉTRICAS DE PRODUCTIVIDAD ==========
    
    @Query("SELECT t.id, t.firstName, t.lastName, COUNT(wo), AVG(CAST(FUNCTION('TIMESTAMPDIFF', HOUR, wo.executionWindow.start, wo.executionWindow.end) AS double)) " +
           "FROM Technician t " +
           "JOIN WorkOrder wo ON wo.tenantId.value = t.tenantId.value " +
           "JOIN wo.technicians wt ON wt.technicianId.value = t.id " +
           "WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL AND t.tenantId.value = :tenantId " +
           "GROUP BY t.id, t.firstName, t.lastName")
    List<Object[]> getTechnicianProductivityMetrics(@Param("tenantId") String tenantId);
    
    @Query("SELECT t.id, t.firstName, t.lastName, COUNT(wo), AVG(CAST(FUNCTION('TIMESTAMPDIFF', HOUR, wo.executionWindow.start, wo.executionWindow.end) AS double)) " +
           "FROM Technician t " +
           "JOIN WorkOrder wo ON wo.tenantId.value = t.tenantId.value " +
           "JOIN wo.technicians wt ON wt.technicianId.value = t.id " +
           "WHERE wo.status = 'COMPLETED' AND wo.executionWindow.end IS NOT NULL " +
           "AND wo.executionWindow.start BETWEEN :startDate AND :endDate AND t.tenantId.value = :tenantId " +
           "GROUP BY t.id, t.firstName, t.lastName")
    List<Object[]> getTechnicianPerformanceInPeriod(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 