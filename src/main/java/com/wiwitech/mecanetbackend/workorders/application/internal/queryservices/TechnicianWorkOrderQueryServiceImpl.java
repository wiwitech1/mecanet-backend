package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TechnicianWorkOrderQueryServiceImpl {
    
    private final TechnicianRepository technicianRepository;
    private final WorkOrderRepository workOrderRepository;
    private final TechnicianMetricsRepository technicianMetricsRepository;
    
    public TechnicianWorkOrderQueryServiceImpl(
            TechnicianRepository technicianRepository,
            WorkOrderRepository workOrderRepository,
            TechnicianMetricsRepository technicianMetricsRepository) {
        this.technicianRepository = technicianRepository;
        this.workOrderRepository = workOrderRepository;
        this.technicianMetricsRepository = technicianMetricsRepository;
    }
    
    // === GESTIÓN BÁSICA DE TÉCNICOS ===
    
    public List<Technician> getAvailableTechnicians(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        return technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong);
    }
    
    public List<Technician> getTechniciansBySkill(String tenantId, SkillId skillId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        return technicianRepository.findBySkillAndTenantId(skillId, tenantIdLong);
    }
    
    public List<Technician> getTechniciansWithMinimumSkills(String tenantId, int minSkills) {
        Long tenantIdLong = Long.valueOf(tenantId);
        return technicianRepository.findTechniciansWithMinimumSkills(minSkills, tenantIdLong);
    }
    
    public List<Technician> getLeastBusyTechnicians(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        return technicianRepository.findLeastBusyTechnicians(tenantIdLong);
    }
    
    // === ASIGNACIÓN INTELIGENTE BÁSICA ===
    
    public List<Technician> getRecommendedTechnicians(String tenantId, String workOrderId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        Long workOrderIdLong = Long.valueOf(workOrderId);
        
        Optional<WorkOrder> workOrder = workOrderRepository.findByWorkOrderIdValueAndTenantIdValue(workOrderIdLong, tenantIdLong);
        if (workOrder.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Obtener técnicos disponibles con skills requeridas
        Set<SkillId> requiredSkills = workOrder.get().getRequiredSkillIds();
        return technicianRepository.findAvailableWithSkills(requiredSkills, tenantIdLong);
    }
    
    // === ANÁLISIS DE CARGA DE TRABAJO BÁSICO ===
    
    public Map<String, Object> getTechnicianWorkload(String tenantId, Long technicianId) {
        Map<String, Object> workload = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Contar órdenes activas usando métodos existentes
        long activeOrders = workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.IN_EXECUTION, tenantIdLong);
        workload.put("activeOrders", activeOrders);
        
        // Contar órdenes programadas
        long scheduledOrders = workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong);
        workload.put("scheduledOrders", scheduledOrders);
        
        // Capacidad estimada (placeholder)
        double capacity = (activeOrders + scheduledOrders) / 5.0 * 100; // Asumiendo max 5 órdenes
        workload.put("capacityUtilization", capacity);
        
        return workload;
    }
    
    public List<Map<String, Object>> getAllTechniciansWorkload(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        List<Technician> technicians = technicianRepository.findAll().stream()
                .filter(tech -> tech.getTenantId().getValue().equals(tenantIdLong))
                .collect(Collectors.toList());
        
        return technicians.stream()
                .map(tech -> {
                    Map<String, Object> workload = getTechnicianWorkload(tenantId, tech.getId());
                    workload.put("technicianId", tech.getId());
                    workload.put("technicianName", tech.getFirstName() + " " + tech.getLastName());
                    workload.put("status", tech.getCurrentStatus());
                    return workload;
                })
                .collect(Collectors.toList());
    }
    
    // === MÉTRICAS DE PERFORMANCE BÁSICAS ===
    
    public Map<String, Object> getTechnicianPerformanceMetrics(String tenantId, Long technicianId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Usar métricas básicas disponibles
        List<Object[]> productivityMetrics = technicianMetricsRepository.getTechnicianProductivityMetrics(tenantId);
        
        // Filtrar por técnico específico
        Optional<Object[]> technicianMetrics = productivityMetrics.stream()
                .filter(metric -> ((Long) metric[0]).equals(technicianId))
                .findFirst();
        
        if (technicianMetrics.isPresent()) {
            Object[] metrics_data = technicianMetrics.get();
            metrics.put("technicianId", metrics_data[0]);
            metrics.put("firstName", metrics_data[1]);
            metrics.put("lastName", metrics_data[2]);
            metrics.put("completedOrders", metrics_data[3]);
            metrics.put("averageCompletionTime", metrics_data[4]);
        } else {
            metrics.put("completedOrders", 0);
            metrics.put("averageCompletionTime", 0.0);
        }
        
        return metrics;
    }
    
    // === ANÁLISIS DE SKILLS ===
    
    public Map<String, List<Technician>> getTechniciansBySkillDistribution(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        List<Object[]> skillDistribution = technicianRepository.getSkillDistribution(tenantIdLong);
        
        Map<String, List<Technician>> result = new HashMap<>();
        
        for (Object[] skillData : skillDistribution) {
            SkillId skillId = (SkillId) skillData[0];
            List<Technician> techniciansWithSkill = technicianRepository.findBySkillAndTenantId(skillId, tenantIdLong);
            result.put(skillId.toString(), techniciansWithSkill);
        }
        
        return result;
    }
    
    // === PLANIFICACIÓN DE CAPACIDAD ===
    
    public Map<String, Object> getTechnicianCapacityPlanning(String tenantId) {
        Map<String, Object> planning = new HashMap<>();
        
        // Técnicos disponibles
        List<Technician> availableTechnicians = getAvailableTechnicians(tenantId);
        planning.put("availableTechnicians", availableTechnicians.size());
        
        // Técnicos ocupados
        Long tenantIdLong = Long.valueOf(tenantId);
        List<Technician> busyTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.BUSY, tenantIdLong);
        planning.put("busyTechnicians", busyTechnicians.size());
        
        // Utilización total
        double utilizationRate = technicianMetricsRepository.getTechnicianUtilizationRate(tenantId);
        planning.put("utilizationRate", utilizationRate);
        
        // Distribución por status
        List<Object[]> statusDistribution = technicianRepository.countTechniciansByStatus(tenantIdLong);
        planning.put("statusDistribution", statusDistribution);
        
        return planning;
    }
    
    // === COLABORACIÓN Y EQUIPOS BÁSICO ===
    
    public List<Map<String, Object>> getTeamFormationRecommendations(String tenantId, Set<SkillId> requiredSkills) {
        Long tenantIdLong = Long.valueOf(tenantId);
        List<Technician> availableTechnicians = technicianRepository.findAvailableWithSkills(requiredSkills, tenantIdLong);
        
        return availableTechnicians.stream()
                .map(tech -> {
                    Map<String, Object> recommendation = new HashMap<>();
                    recommendation.put("technicianId", tech.getId());
                    recommendation.put("name", tech.getFirstName() + " " + tech.getLastName());
                    recommendation.put("skills", tech.getSkills());
                    recommendation.put("status", tech.getCurrentStatus());
                    recommendation.put("recommendationScore", calculateBasicScore(tech, requiredSkills));
                    return recommendation;
                })
                .sorted((r1, r2) -> Double.compare((Double) r2.get("recommendationScore"), (Double) r1.get("recommendationScore")))
                .collect(Collectors.toList());
    }
    
    private double calculateBasicScore(Technician technician, Set<SkillId> requiredSkills) {
        // Score básico basado en skills matching
        long matchingSkills = technician.getSkills().stream()
                .mapToLong(skill -> requiredSkills.contains(skill) ? 1 : 0)
                .sum();
        
        return requiredSkills.isEmpty() ? 0.5 : (double) matchingSkills / requiredSkills.size();
    }
} 