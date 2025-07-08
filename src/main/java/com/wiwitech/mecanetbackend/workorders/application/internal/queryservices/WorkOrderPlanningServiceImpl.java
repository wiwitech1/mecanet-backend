package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderPlanningServiceImpl {
    
    private final WorkOrderRepository workOrderRepository;
    private final TechnicianRepository technicianRepository;
    
    public WorkOrderPlanningServiceImpl(
            WorkOrderRepository workOrderRepository,
            TechnicianRepository technicianRepository) {
        this.workOrderRepository = workOrderRepository;
        this.technicianRepository = technicianRepository;
    }
    
    // === PLANIFICACIÓN ESTRATÉGICA ===
    
    public Map<String, Object> getStrategicPlan(String tenantId, int daysAhead) {
        Map<String, Object> plan = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Órdenes por estado
        Map<String, Long> ordersByStatus = new HashMap<>();
        ordersByStatus.put("NEW", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.NEW, tenantIdLong));
        ordersByStatus.put("PUBLISHED", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong));
        ordersByStatus.put("IN_EXECUTION", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.IN_EXECUTION, tenantIdLong));
        ordersByStatus.put("COMPLETED", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.COMPLETED, tenantIdLong));
        
        plan.put("ordersByStatus", ordersByStatus);
        
        // Capacidad de técnicos
        Map<String, Long> technicianCapacity = new HashMap<>();
        long availableCount = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong).size();
        long busyCount = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.BUSY, tenantIdLong).size();
        long offlineCount = technicianRepository.findAll().stream()
                .filter(tech -> tech.getTenantId().getValue().equals(tenantIdLong) && 
                               tech.getCurrentStatus() != TechnicianStatus.AVAILABLE && 
                               tech.getCurrentStatus() != TechnicianStatus.BUSY)
                .count();
        
        technicianCapacity.put("AVAILABLE", availableCount);
        technicianCapacity.put("BUSY", busyCount);
        technicianCapacity.put("OFFLINE", offlineCount);
        
        plan.put("technicianCapacity", technicianCapacity);
        
        // Planificación por máquinas
        List<WorkOrder> publishedOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong);
        Map<String, Long> ordersByMachine = publishedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getMachineId().getValue().toString(),
                        Collectors.counting()
                ));
        
        plan.put("ordersByMachine", ordersByMachine);
        
        // Recomendaciones básicas
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Evaluar redistribución de carga de trabajo");
        recommendations.add("Considerar capacitación adicional para técnicos");
        recommendations.add("Revisar programación de mantenimiento preventivo");
        
        plan.put("recommendations", recommendations);
        
        return plan;
    }
    
    // === OPTIMIZACIÓN DE HORARIOS ===
    
    public Map<String, Object> getScheduleOptimization(String tenantId) {
        Map<String, Object> optimization = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Órdenes pendientes de programación
        List<WorkOrder> newOrders = workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.NEW, tenantIdLong);
        optimization.put("ordersToSchedule", newOrders.size());
        
        // Técnicos disponibles para asignación
        List<Technician> availableTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong);
        optimization.put("availableTechnicians", availableTechnicians.size());
        
        // Sugerencias de optimización
        List<Map<String, Object>> suggestions = new ArrayList<>();
        for (WorkOrder order : newOrders.subList(0, Math.min(5, newOrders.size()))) {
            Map<String, Object> suggestion = new HashMap<>();
            suggestion.put("workOrderId", order.getWorkOrderId().getValue());
            suggestion.put("title", order.getTitle());
            suggestion.put("machineId", order.getMachineId().getValue());
            suggestion.put("requiredSkills", order.getRequiredSkillIds().stream()
                    .map(SkillId::toString)
                    .collect(Collectors.toList()));
            suggestion.put("recommendedTechnicians", findSuitableTechnicians(order, availableTechnicians));
            suggestions.add(suggestion);
        }
        
        optimization.put("schedulingSuggestions", suggestions);
        
        return optimization;
    }
    
    private List<Map<String, Object>> findSuitableTechnicians(WorkOrder workOrder, List<Technician> availableTechnicians) {
        return availableTechnicians.stream()
                .map(tech -> {
                    Map<String, Object> techInfo = new HashMap<>();
                    techInfo.put("technicianId", tech.getId());
                    techInfo.put("name", tech.getFirstName() + " " + tech.getLastName());
                    techInfo.put("skills", tech.getSkills().stream()
                            .map(SkillId::toString)
                            .collect(Collectors.toList()));
                    techInfo.put("matchScore", calculateSkillMatch(workOrder.getRequiredSkillIds(), tech.getSkills()));
                    return techInfo;
                })
                .sorted((t1, t2) -> Double.compare((Double) t2.get("matchScore"), (Double) t1.get("matchScore")))
                .limit(3)
                .collect(Collectors.toList());
    }
    
    private double calculateSkillMatch(Set<SkillId> requiredSkills, Set<SkillId> technicianSkills) {
        if (requiredSkills.isEmpty()) return 0.5;
        
        long matchingSkills = technicianSkills.stream()
                .mapToLong(skill -> requiredSkills.contains(skill) ? 1 : 0)
                .sum();
        
        return (double) matchingSkills / requiredSkills.size();
    }
    
    // === IDENTIFICACIÓN DE CUELLOS DE BOTELLA ===
    
    public Map<String, Object> getBottleneckAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Análisis de estados
        Map<String, Long> statusBottlenecks = new HashMap<>();
        statusBottlenecks.put("PENDING_EXECUTION", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PENDING_EXECUTION, tenantIdLong));
        statusBottlenecks.put("IN_EXECUTION", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.IN_EXECUTION, tenantIdLong));
        statusBottlenecks.put("REVIEW", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.REVIEW, tenantIdLong));
        
        analysis.put("statusBottlenecks", statusBottlenecks);
        
        // Análisis de recursos
        Map<String, Object> resourceBottlenecks = new HashMap<>();
        resourceBottlenecks.put("availableTechnicians", (long) technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong).size());
        resourceBottlenecks.put("busyTechnicians", (long) technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.BUSY, tenantIdLong).size());
        
        analysis.put("resourceBottlenecks", resourceBottlenecks);
        
        // Identificar cuellos de botella críticos
        List<String> criticalBottlenecks = new ArrayList<>();
        if (statusBottlenecks.get("PENDING_EXECUTION") > 10) {
            criticalBottlenecks.add("Alto número de órdenes pendientes de ejecución");
        }
        if (statusBottlenecks.get("IN_EXECUTION") > 20) {
            criticalBottlenecks.add("Muchas órdenes en ejecución simultánea");
        }
        if ((Long) resourceBottlenecks.get("availableTechnicians") < 3) {
            criticalBottlenecks.add("Pocos técnicos disponibles");
        }
        
        analysis.put("criticalBottlenecks", criticalBottlenecks);
        
        return analysis;
    }
    
    // === SIMULACIÓN DE ESCENARIOS ===
    
    public Map<String, Object> getScenarioSimulation(String tenantId, String scenarioType) {
        Map<String, Object> simulation = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Datos base
        long totalOrders = workOrderRepository.count();
        long activeTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong).size();
        
        switch (scenarioType.toUpperCase()) {
            case "PEAK_LOAD":
                simulation.put("scenario", "Carga Pico");
                simulation.put("estimatedOrders", totalOrders * 1.5);
                simulation.put("recommendedTechnicians", activeTechnicians * 1.3);
                simulation.put("estimatedDuration", "2-3 días adicionales");
                break;
                
            case "TECHNICIAN_SHORTAGE":
                simulation.put("scenario", "Escasez de Técnicos");
                simulation.put("availableTechnicians", activeTechnicians * 0.7);
                simulation.put("estimatedDelay", "30-40% más tiempo");
                simulation.put("recommendedActions", Arrays.asList("Contratar técnicos temporales", "Redistribuir cargas"));
                break;
                
            case "EMERGENCY_RESPONSE":
                simulation.put("scenario", "Respuesta de Emergencia");
                simulation.put("emergencyOrders", totalOrders * 0.2);
                simulation.put("responseTime", "1-2 horas");
                simulation.put("resourceReallocation", "Reasignar 80% de técnicos disponibles");
                break;
        }
        
        return simulation;
    }
    
    // === PREVISIÓN DE RECURSOS ===
    
    public Map<String, Object> getResourceForecasting(String tenantId, int daysAhead) {
        Map<String, Object> forecasting = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Proyección basada en datos actuales
        long currentOrders = workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong);
        long currentTechnicians = technicianRepository.findByCurrentStatusAndTenantIdValue(TechnicianStatus.AVAILABLE, tenantIdLong).size();
        
        // Proyecciones simples
        Map<String, Object> projections = new HashMap<>();
        projections.put("estimatedNewOrders", currentOrders * 1.1 * daysAhead / 7); // Estimación semanal
        projections.put("requiredTechnicians", Math.max(currentTechnicians, (long) (currentOrders * 0.3)));
        projections.put("estimatedCompletionTime", daysAhead * 0.8); // 80% del tiempo proyectado
        
        forecasting.put("projections", projections);
        
        // Recomendaciones de recursos
        List<String> resourceRecommendations = new ArrayList<>();
        resourceRecommendations.add("Mantener al menos " + (currentTechnicians + 2) + " técnicos disponibles");
        resourceRecommendations.add("Considerar capacitación cruzada para flexibilidad");
        resourceRecommendations.add("Evaluar necesidad de equipos adicionales");
        
        forecasting.put("recommendations", resourceRecommendations);
        
        return forecasting;
    }
} 