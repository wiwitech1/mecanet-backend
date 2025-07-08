package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderMetricsQueryServiceImpl {
    
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderMetricsRepository workOrderMetricsRepository;
    
    public WorkOrderMetricsQueryServiceImpl(
            WorkOrderRepository workOrderRepository,
            WorkOrderMetricsRepository workOrderMetricsRepository) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderMetricsRepository = workOrderMetricsRepository;
    }
    
    // === MÉTRICAS BÁSICAS ===
    
    public Map<String, Object> getBasicMetrics(String tenantId) {
        Map<String, Object> metrics = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Contadores básicos usando métodos existentes
        metrics.put("totalWorkOrders", workOrderRepository.count());
        metrics.put("activeWorkOrders", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.IN_EXECUTION, tenantIdLong));
        metrics.put("publishedWorkOrders", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.PUBLISHED, tenantIdLong));
        metrics.put("completedWorkOrders", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.COMPLETED, tenantIdLong));
        
        // Métricas de tiempo usando repository personalizado
        metrics.put("averageCompletionTime", workOrderMetricsRepository.getAverageCompletionTimeInHours(tenantId));
        metrics.put("averageResolutionTime", workOrderMetricsRepository.getAverageResolutionTime(tenantId));
        
        return metrics;
    }
    
    // === MÉTRICAS TEMPORALES ===
    
    public Map<String, Object> getTemporalMetrics(String tenantId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> metrics = new HashMap<>();
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        // Usar métricas del repository personalizado
        metrics.put("totalCost", workOrderMetricsRepository.getTotalCostInPeriod(tenantId, start, end));
        metrics.put("averageCompletionTime", workOrderMetricsRepository.getAverageCompletionTimeInPeriod(tenantId, start, end));
        metrics.put("ordersByCategory", workOrderMetricsRepository.getOrdersByCategoryInPeriod(tenantId, start, end));
        
        return metrics;
    }
    
    // === MÉTRICAS DE PERFORMANCE ===
    
    public Map<String, Object> getPerformanceMetrics(String tenantId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Usar métricas disponibles en el repository
        metrics.put("firstTimeFixRate", workOrderMetricsRepository.getFirstTimeFixRate(tenantId));
        metrics.put("averageCostPerOrder", workOrderMetricsRepository.getAverageCostPerOrder(tenantId));
        metrics.put("completionRateLastMonth", workOrderMetricsRepository.getCompletionRateLastMonth(tenantId, 
            LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        
        return metrics;
    }
    
    // === ANÁLISIS DE TIEMPO ===
    
    public Map<String, Object> getTimeAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Órdenes atrasadas usando método existente
        List<WorkOrder> delayedOrders = workOrderRepository.findDelayedWorkOrders(tenantIdLong);
        analysis.put("delayedOrders", delayedOrders.size());
        analysis.put("delayedOrdersList", delayedOrders.stream()
                .map(order -> Map.of(
                        "workOrderId", order.getWorkOrderId().getValue(),
                        "title", order.getTitle(),
                        "status", order.getStatus(),
                        "machineId", order.getMachineId().getValue()
                ))
                .collect(Collectors.toList()));
        
        // Tiempo promedio por estado
        analysis.put("averageTimeByStatus", workOrderMetricsRepository.getAverageTimeByStatus(tenantId));
        
        return analysis;
    }
    
    // === ANÁLISIS DE TENDENCIAS ===
    
    public List<Object[]> getTrendAnalysis(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return workOrderMetricsRepository.getMonthlyTrends(tenantId, startDate, endDate);
    }
    
    // === MÉTRICAS DE CALIDAD ===
    
    public Map<String, Object> getQualityMetrics(String tenantId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Métricas básicas de calidad
        metrics.put("averageResolutionTime", workOrderMetricsRepository.getAverageResolutionTime(tenantId));
        metrics.put("firstTimeFixRate", workOrderMetricsRepository.getFirstTimeFixRate(tenantId));
        metrics.put("averageCostPerOrder", workOrderMetricsRepository.getAverageCostPerOrder(tenantId));
        
        return metrics;
    }
    
    // === ANÁLISIS POR PRIORIDAD ===
    
    public Map<String, Object> getPriorityAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Distribución por prioridad
        analysis.put("ordersByPriority", workOrderMetricsRepository.getOrdersByPriority(tenantId));
        
        return analysis;
    }
    
    // === ANÁLISIS DE MÁQUINAS ===
    
    public Map<String, Object> getMachineAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Top máquinas con más órdenes
        analysis.put("topMachinesWithMostOrders", workOrderMetricsRepository.getTopMachinesWithMostOrders(tenantId, 10));
        
        // Máquinas con alto riesgo
        analysis.put("highRiskMachines", workOrderMetricsRepository.getHighRiskMachines(tenantId));
        
        return analysis;
    }
    
    // === ANÁLISIS DE MATERIALES ===
    
    public Map<String, Object> getMaterialsAnalysis(String tenantId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analysis = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Materiales más utilizados
        analysis.put("mostUsedMaterials", workOrderMetricsRepository.getMostUsedMaterials(startDate, endDate, tenantIdLong));
        
        return analysis;
    }
    
    // === ANÁLISIS PREDICTIVO BÁSICO ===
    
    public Map<String, Object> getPredictiveAnalysis(String tenantId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Predicción de carga de trabajo
        LocalDateTime forecastStart = LocalDateTime.now().minusDays(30);
        analysis.put("workloadForecast", workOrderMetricsRepository.getWorkloadForecast(tenantId, forecastStart));
        
        // Recomendaciones de mantenimiento preventivo
        analysis.put("preventiveMaintenanceRecommendations", 
                workOrderMetricsRepository.getPreventiveMaintenanceRecommendations(tenantId));
        
        // Análisis de patrones de falla
        analysis.put("failurePatterns", workOrderMetricsRepository.getFailurePatterns(tenantId));
        
        return analysis;
    }
} 