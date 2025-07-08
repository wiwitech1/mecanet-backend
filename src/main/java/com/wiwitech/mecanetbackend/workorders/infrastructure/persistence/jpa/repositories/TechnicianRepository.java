package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    
    // ========== QUERIES BÁSICAS EXISTENTES ==========
    boolean existsByIamUserIdValueAndTenantIdValue(Long iamUserId, Long tenantId);
    Optional<Technician> findByIamUserIdValueAndTenantIdValue(Long iamUserId, Long tenantId);
    List<Technician> findByCurrentStatusAndTenantIdValue(TechnicianStatus status, Long tenantId);
    
    @Query("SELECT t FROM Technician t JOIN t.skills s WHERE s = :skillId AND t.tenantId.value = :tenantId")
    List<Technician> findBySkillAndTenantId(@Param("skillId") SkillId skillId, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA ASIGNACIÓN INTELIGENTE ==========
    
    /**
     * Buscar técnicos disponibles por turno
     */
    @Query("SELECT t FROM Technician t WHERE t.currentStatus = :status AND t.shift = :shift AND t.tenantId.value = :tenantId")
    List<Technician> findByStatusAndShiftAndTenantId(@Param("status") TechnicianStatus status, @Param("shift") Shift shift, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos disponibles con skills específicas
     */
    @Query("SELECT DISTINCT t FROM Technician t JOIN t.skills s WHERE t.currentStatus = 'AVAILABLE' " +
           "AND s IN :skillIds AND t.tenantId.value = :tenantId")
    List<Technician> findAvailableWithSkills(@Param("skillIds") Set<SkillId> skillIds, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos con múltiples skills (polivalentes)
     */
    @Query("SELECT t FROM Technician t WHERE t.tenantId.value = :tenantId AND SIZE(t.skills) >= :minSkills")
    List<Technician> findTechniciansWithMinimumSkills(@Param("minSkills") int minSkills, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos disponibles que tengan TODAS las skills requeridas
     */
    @Query("SELECT t FROM Technician t WHERE t.currentStatus = 'AVAILABLE' AND t.tenantId.value = :tenantId " +
           "AND (SELECT COUNT(s) FROM t.skills s WHERE s IN :requiredSkills) = :skillCount")
    List<Technician> findAvailableWithAllRequiredSkills(@Param("requiredSkills") Set<SkillId> requiredSkills, 
                                                        @Param("skillCount") long skillCount, 
                                                        @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos con menor carga de trabajo (menos work orders activos)
     */
    @Query("SELECT t FROM Technician t WHERE t.currentStatus = 'AVAILABLE' AND t.tenantId.value = :tenantId " +
           "ORDER BY (SELECT COUNT(wo) FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wt.technicianId.value = t.id AND wo.status IN ('PUBLISHED', 'REVIEW', 'PENDING_EXECUTION', 'IN_EXECUTION')) ASC")
    List<Technician> findLeastBusyTechnicians(@Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA GESTIÓN DE SUPERVISORES ==========
    
    /**
     * Buscar técnicos supervisados por un supervisor específico
     */
    @Query("SELECT t FROM Technician t WHERE t.supervisor.id = :supervisorId AND t.tenantId.value = :tenantId")
    List<Technician> findBySupervisorIdAndTenantId(@Param("supervisorId") Long supervisorId, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar supervisores disponibles
     */
    @Query("SELECT t FROM Technician t WHERE t.currentStatus = 'AVAILABLE' AND t.tenantId.value = :tenantId " +
           "AND EXISTS (SELECT 1 FROM Technician sub WHERE sub.supervisor.id = t.id)")
    List<Technician> findAvailableSupervisors(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos sin supervisor asignado
     */
    @Query("SELECT t FROM Technician t WHERE t.supervisor IS NULL AND t.tenantId.value = :tenantId")
    List<Technician> findWithoutSupervisor(@Param("tenantId") Long tenantId);
    
    /**
     * Contar técnicos supervisados por cada supervisor
     */
    @Query("SELECT t.supervisor.id, COUNT(t) FROM Technician t WHERE t.supervisor IS NOT NULL AND t.tenantId.value = :tenantId " +
           "GROUP BY t.supervisor.id")
    List<Object[]> countTechniciansBySupervisor(@Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA BÚSQUEDAS AVANZADAS ==========
    
    /**
     * Buscar técnicos por múltiples criterios (status, shift, skills)
     */
    @Query("SELECT DISTINCT t FROM Technician t LEFT JOIN t.skills s WHERE t.tenantId.value = :tenantId " +
           "AND (:status IS NULL OR t.currentStatus = :status) " +
           "AND (:shift IS NULL OR t.shift = :shift) " +
           "AND (:skillIds IS NULL OR s IN :skillIds)")
    List<Technician> findByMultipleCriteria(@Param("status") TechnicianStatus status,
                                           @Param("shift") Shift shift,
                                           @Param("skillIds") Set<SkillId> skillIds,
                                           @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos por nombre o username (para búsqueda de texto)
     */
    @Query("SELECT t FROM Technician t WHERE t.tenantId.value = :tenantId " +
           "AND (LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(t.username) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Technician> findByNameOrUsername(@Param("searchText") String searchText, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA MÉTRICAS Y ESTADÍSTICAS ==========
    
    /**
     * Contar técnicos por estado
     */
    @Query("SELECT t.currentStatus, COUNT(t) FROM Technician t WHERE t.tenantId.value = :tenantId GROUP BY t.currentStatus")
    List<Object[]> countTechniciansByStatus(@Param("tenantId") Long tenantId);
    
    /**
     * Contar técnicos por turno
     */
    @Query("SELECT t.shift, COUNT(t) FROM Technician t WHERE t.tenantId.value = :tenantId GROUP BY t.shift")
    List<Object[]> countTechniciansByShift(@Param("tenantId") Long tenantId);
    
    /**
     * Obtener distribución de skills (qué skills son más comunes)
     */
    @Query("SELECT s, COUNT(t) FROM Technician t JOIN t.skills s WHERE t.tenantId.value = :tenantId GROUP BY s ORDER BY COUNT(t) DESC")
    List<Object[]> getSkillDistribution(@Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos más activos (con más work orders completados)
     */
    @Query("SELECT t, COUNT(wo) as workOrderCount FROM Technician t " +
           "JOIN WorkOrder wo ON wo.tenantId.value = t.tenantId.value " +
           "JOIN wo.technicians wt ON wt.technicianId.value = t.id " +
           "WHERE wo.status = 'COMPLETED' AND t.tenantId.value = :tenantId " +
           "GROUP BY t ORDER BY workOrderCount DESC")
    List<Object[]> findMostActiveTechnicians(@Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA PLANIFICACIÓN Y DISPONIBILIDAD ==========
    
    /**
     * Buscar técnicos disponibles para un turno específico que no estén en work orders activos
     */
    @Query("SELECT t FROM Technician t WHERE t.currentStatus = 'AVAILABLE' AND t.shift = :shift AND t.tenantId.value = :tenantId " +
           "AND NOT EXISTS (SELECT 1 FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wt.technicianId.value = t.id AND wo.status IN ('PENDING_EXECUTION', 'IN_EXECUTION'))")
    List<Technician> findAvailableForShiftWithoutActiveWorkOrders(@Param("shift") Shift shift, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos que pueden ser supervisores (tienen experiencia)
     */
    @Query("SELECT t FROM Technician t WHERE t.tenantId.value = :tenantId " +
           "AND (SELECT COUNT(wo) FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wt.technicianId.value = t.id AND wo.status = 'COMPLETED') >= :minCompletedWorkOrders")
    List<Technician> findPotentialSupervisors(@Param("minCompletedWorkOrders") int minCompletedWorkOrders, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos con skills complementarias (para formar equipos)
     */
    @Query("SELECT t FROM Technician t WHERE t.currentStatus = 'AVAILABLE' AND t.tenantId.value = :tenantId " +
           "AND EXISTS (SELECT 1 FROM t.skills s WHERE s NOT IN :existingSkills)")
    List<Technician> findWithComplementarySkills(@Param("existingSkills") Set<SkillId> existingSkills, @Param("tenantId") Long tenantId);
    
    // ========== QUERIES PARA AUDITORÍA Y REPORTES ==========
    
    /**
     * Buscar técnicos que no han trabajado recientemente
     */
    @Query("SELECT t FROM Technician t WHERE t.tenantId.value = :tenantId " +
           "AND NOT EXISTS (SELECT 1 FROM WorkOrder wo JOIN wo.technicians wt " +
           "WHERE wt.technicianId.value = t.id AND wo.status = 'COMPLETED' " +
           "AND wo.executionWindow.end > :cutoffDate)")
    List<Technician> findInactiveTechnicians(@Param("cutoffDate") java.time.LocalDateTime cutoffDate, @Param("tenantId") Long tenantId);
    
    /**
     * Buscar técnicos con información incompleta (sin teléfono, sin skills, etc.)
     */
    @Query("SELECT t FROM Technician t WHERE t.tenantId.value = :tenantId " +
           "AND (t.phoneNumber IS NULL OR SIZE(t.skills) = 0)")
    List<Technician> findWithIncompleteProfile(@Param("tenantId") Long tenantId);
}