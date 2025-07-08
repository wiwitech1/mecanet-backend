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
public class TechnicianAdvancedQueryServiceImpl {
    
    private final TechnicianRepository technicianRepository;
    private final WorkOrderRepository workOrderRepository;
    private final TechnicianMetricsRepository technicianMetricsRepository;
    
    public TechnicianAdvancedQueryServiceImpl(
            TechnicianRepository technicianRepository,
            WorkOrderRepository workOrderRepository,
            TechnicianMetricsRepository technicianMetricsRepository) {
        this.technicianRepository = technicianRepository;
        this.workOrderRepository = workOrderRepository;
        this.technicianMetricsRepository = technicianMetricsRepository;
    }
    
    // === RECOMENDACIONES INTELIGENTES ===
    
    public List<Map<String, Object>> getIntelligentTechnicianRecommendations(String tenantId, String workOrderId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        Long workOrderIdLong = Long.valueOf(workOrderId);
        
        Optional<WorkOrder> workOrder = workOrderRepository.findByWorkOrderIdValueAndTenantIdValue(workOrderIdLong, tenantIdLong);
        if (workOrder.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Technician> availableTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong);
        
        return availableTechnicians.stream()
                .map(tech -> {
                    Map<String, Object> recommendation = new HashMap<>();
                    recommendation.put("technicianId", tech.getId());
                    recommendation.put("name", tech.getFirstName() + " " + tech.getLastName());
                    recommendation.put("skills", tech.getSkills());
                    recommendation.put("status", tech.getCurrentStatus());
                    recommendation.put("matchScore", calculateSkillMatchScore(tech, workOrder.get()));
                    recommendation.put("workloadScore", calculateWorkloadScore(tech));
                    recommendation.put("overallScore", calculateOverallScore(tech, workOrder.get()));
                    return recommendation;
                })
                .sorted((r1, r2) -> Double.compare((Double) r2.get("overallScore"), (Double) r1.get("overallScore")))
                .collect(Collectors.toList());
    }
    
    private double calculateSkillMatchScore(Technician technician, WorkOrder workOrder) {
        Set<SkillId> requiredSkills = workOrder.getRequiredSkillIds();
        Set<SkillId> technicianSkills = technician.getSkills();
        
        if (requiredSkills.isEmpty()) {
            return 0.5;
        }
        
        long matchingSkills = technicianSkills.stream()
                .mapToLong(skill -> requiredSkills.contains(skill) ? 1 : 0)
                .sum();
        
        return (double) matchingSkills / requiredSkills.size();
    }
    
    private double calculateWorkloadScore(Technician technician) {
        // Score básico basado en disponibilidad
        return technician.getCurrentStatus() == TechnicianStatus.AVAILABLE ? 1.0 : 0.2;
    }
    
    private double calculateOverallScore(Technician technician, WorkOrder workOrder) {
        double skillScore = calculateSkillMatchScore(technician, workOrder) * 0.6;
        double workloadScore = calculateWorkloadScore(technician) * 0.4;
        return skillScore + workloadScore;
    }
    
    // === OPTIMIZACIÓN DE ASIGNACIONES ===
    
    public List<Map<String, Object>> getOptimalAssignments(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        List<WorkOrder> publishedOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong);
        List<Technician> availableTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong);
        
        List<Map<String, Object>> assignments = new ArrayList<>();
        
        for (WorkOrder order : publishedOrders) {
            List<Technician> suitableTechnicians = availableTechnicians.stream()
                    .filter(tech -> calculateSkillMatchScore(tech, order) > 0.5)
                    .sorted((t1, t2) -> Double.compare(calculateOverallScore(t2, order), calculateOverallScore(t1, order)))
                    .limit(3)
                    .collect(Collectors.toList());
            
            if (!suitableTechnicians.isEmpty()) {
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("workOrderId", order.getWorkOrderId().getValue());
                assignment.put("workOrderTitle", order.getTitle());
                assignment.put("recommendedTechnicians", suitableTechnicians.stream()
                        .map(tech -> Map.of(
                                "technicianId", tech.getId(),
                                "name", tech.getFirstName() + " " + tech.getLastName(),
                                "score", calculateOverallScore(tech, order)
                        ))
                        .collect(Collectors.toList()));
                assignments.add(assignment);
            }
        }
        
        return assignments;
    }
    
    // === ANÁLISIS DE CAPACIDAD ===
    
    public Map<String, Object> getCapacityAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Utilización total
        double utilizationRate = technicianMetricsRepository.getTechnicianUtilizationRate(tenantId);
        analysis.put("overallUtilization", utilizationRate);
        
        // Distribución de carga
        List<Object[]> workloadDistribution = technicianMetricsRepository.getTechnicianWorkloadDistribution(tenantId);
        analysis.put("workloadDistribution", workloadDistribution);
        
        // Técnicos disponibles
        Long availableCount = technicianMetricsRepository.getAvailableTechniciansCount(tenantId);
        analysis.put("availableTechnicians", availableCount);
        
        // Métricas de productividad
        List<Object[]> productivityMetrics = technicianMetricsRepository.getTechnicianProductivityMetrics(tenantId);
        analysis.put("productivityMetrics", productivityMetrics);
        
        return analysis;
    }
    
    // === ANÁLISIS DE SKILLS ===
    
    public Map<String, Object> getSkillsGapAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Distribución de skills actuales
        List<Object[]> skillDistribution = technicianRepository.getSkillDistribution(tenantIdLong);
        analysis.put("currentSkillDistribution", skillDistribution);
        
        // Skills más demandadas (basado en work orders publicadas)
        List<WorkOrder> publishedOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong);
        Map<SkillId, Long> skillDemand = publishedOrders.stream()
                .flatMap(wo -> wo.getRequiredSkillIds().stream())
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        
        analysis.put("skillDemand", skillDemand);
        
        // Identificar gaps
        List<Map<String, Object>> gaps = new ArrayList<>();
        for (Map.Entry<SkillId, Long> demand : skillDemand.entrySet()) {
            SkillId skill = demand.getKey();
            Long demandCount = demand.getValue();
            
            List<Technician> techniciansWithSkill = technicianRepository.findBySkillAndTenantId(skill, tenantIdLong);
            long supply = techniciansWithSkill.size();
            
            if (demandCount > supply) {
                Map<String, Object> gap = new HashMap<>();
                gap.put("skill", skill.toString());
                gap.put("demand", demandCount);
                gap.put("supply", supply);
                gap.put("gap", demandCount - supply);
                gaps.add(gap);
            }
        }
        
        analysis.put("skillGaps", gaps);
        
        return analysis;
    }
    
    // === RECOMENDACIONES DE DESARROLLO ===
    
    public List<Map<String, Object>> getDevelopmentRecommendations(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        List<Technician> technicians = technicianRepository.findAll().stream()
                .filter(tech -> tech.getTenantId().getValue().equals(tenantIdLong))
                .collect(Collectors.toList());
        
        return technicians.stream()
                .map(tech -> {
                    Map<String, Object> recommendation = new HashMap<>();
                    recommendation.put("technicianId", tech.getId());
                    recommendation.put("name", tech.getFirstName() + " " + tech.getLastName());
                    recommendation.put("currentSkills", tech.getSkills());
                    recommendation.put("skillCount", tech.getSkills().size());
                    recommendation.put("developmentPotential", calculateDevelopmentPotential(tech));
                    recommendation.put("recommendedSkills", getRecommendedSkillsForTechnician(tech, tenantId));
                    return recommendation;
                })
                .sorted((r1, r2) -> Double.compare((Double) r2.get("developmentPotential"), (Double) r1.get("developmentPotential")))
                .collect(Collectors.toList());
    }
    
    private double calculateDevelopmentPotential(Technician technician) {
        // Score básico basado en número de skills actuales
        int skillCount = technician.getSkills().size();
        return Math.min(1.0, skillCount / 5.0); // Normalizar a máximo 5 skills
    }
    
    private List<String> getRecommendedSkillsForTechnician(Technician technician, String tenantId) {
        // Placeholder - en una implementación real analizaríamos skills complementarias
        return Arrays.asList("Mantenimiento Preventivo", "Diagnóstico Avanzado", "Automatización");
    }
    
    // === PLANIFICACIÓN DE TURNOS ===
    
    public Map<String, Object> getShiftPlanningRecommendations(String tenantId) {
        Map<String, Object> planning = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Distribución actual por turno
        List<Object[]> shiftDistribution = technicianRepository.countTechniciansByShift(tenantIdLong);
        planning.put("currentShiftDistribution", shiftDistribution);
        
        // Carga de trabajo por turno (basado en work orders)
        Map<String, Long> workloadByShift = new HashMap<>();
        workloadByShift.put("DAY", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong));
        workloadByShift.put("NIGHT", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PENDING_EXECUTION, tenantIdLong));
        planning.put("workloadByShift", workloadByShift);
        
        // Recomendaciones de balanceo
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Considerar redistribuir técnicos entre turnos");
        recommendations.add("Evaluar necesidad de técnicos adicionales en turno nocturno");
        planning.put("recommendations", recommendations);
        
        return planning;
    }
} 