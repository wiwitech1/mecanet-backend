package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderMetricsRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderDashboardQueryServiceImpl {
    
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderMetricsRepository workOrderMetricsRepository;
    private final TechnicianMetricsRepository technicianMetricsRepository;
    
    public WorkOrderDashboardQueryServiceImpl(
            WorkOrderRepository workOrderRepository,
            WorkOrderMetricsRepository workOrderMetricsRepository,
            TechnicianMetricsRepository technicianMetricsRepository) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderMetricsRepository = workOrderMetricsRepository;
        this.technicianMetricsRepository = technicianMetricsRepository;
    }
    
    // === EXECUTIVE DASHBOARD METRICS ===
    
    public Map<String, Object> getExecutiveDashboard(String tenantId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // KPIs principales usando métodos existentes
        dashboard.put("totalWorkOrders", workOrderRepository.count());
        dashboard.put("activeWorkOrders", workOrderRepository.countByStatusAndTenantIdValue(WorkOrderStatus.IN_EXECUTION, tenantIdLong));
        dashboard.put("completedThisMonth", workOrderMetricsRepository.countCompletedInPeriod(tenantId, 
            LocalDate.now().withDayOfMonth(1).atStartOfDay(), LocalDateTime.now()));
        dashboard.put("averageCompletionTime", workOrderMetricsRepository.getAverageCompletionTimeInHours(tenantId));
        dashboard.put("completionRate", workOrderMetricsRepository.getCompletionRateLastMonth(tenantId, 
            LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        
        // Distribución por estado
        dashboard.put("statusDistribution", getWorkOrderStatusDistribution(tenantId));
        
        // Métricas de eficiencia
        dashboard.put("efficiencyMetrics", getEfficiencyMetrics(tenantId));
        
        // Top máquinas con más órdenes
        dashboard.put("topMachinesWithMostOrders", workOrderMetricsRepository.getTopMachinesWithMostOrders(tenantId, 10));
        
        // Técnicos más activos
        dashboard.put("mostActiveTechnicians", technicianMetricsRepository.getMostActiveTechnicians(tenantId, 10));
        
        // Tendencias mensuales
        dashboard.put("monthlyTrends", getMonthlyTrends(tenantId));
        
        return dashboard;
    }
    
    public Map<String, Object> getOperationalDashboard(String tenantId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Órdenes urgentes - usar métodos existentes
        dashboard.put("urgentWorkOrders", workOrderRepository.findByStatusAndTenantIdValue(WorkOrderStatus.PENDING_EXECUTION, tenantIdLong));
        
        // Órdenes atrasadas - usar delayedWorkOrders
        dashboard.put("overdueWorkOrders", workOrderRepository.findDelayedWorkOrders(tenantIdLong));
        
        // Técnicos disponibles
        dashboard.put("availableTechnicians", technicianMetricsRepository.getAvailableTechniciansCount(tenantId));
        
        // Carga de trabajo por técnico
        dashboard.put("technicianWorkload", technicianMetricsRepository.getTechnicianWorkloadDistribution(tenantId));
        
        // Órdenes por prioridad
        dashboard.put("ordersByPriority", workOrderMetricsRepository.getOrdersByPriority(tenantId));
        
        // Materiales más utilizados - usar fechas
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        dashboard.put("mostUsedMaterials", workOrderMetricsRepository.getMostUsedMaterials(startDate, endDate, tenantIdLong));
        
        // Tiempo promedio por estado
        dashboard.put("averageTimeByStatus", workOrderMetricsRepository.getAverageTimeByStatus(tenantId));
        
        return dashboard;
    }
    
    // === MÉTRICAS ESPECÍFICAS ===
    
    public Map<WorkOrderStatus, Long> getWorkOrderStatusDistribution(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        // Usar método existente para obtener todas las órdenes
        List<WorkOrder> workOrders = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getTenantId().getValue().equals(tenantIdLong))
                .collect(Collectors.toList());
        return workOrders.stream()
                .collect(Collectors.groupingBy(WorkOrder::getStatus, Collectors.counting()));
    }
    
    public Map<String, Object> getEfficiencyMetrics(String tenantId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Tiempo promedio de resolución
        metrics.put("averageResolutionTime", workOrderMetricsRepository.getAverageResolutionTime(tenantId));
        
        // Tasa de primera resolución
        metrics.put("firstTimeFixRate", workOrderMetricsRepository.getFirstTimeFixRate(tenantId));
        
        // Utilización de técnicos
        metrics.put("technicianUtilization", technicianMetricsRepository.getTechnicianUtilizationRate(tenantId));
        
        // Costo promedio por orden
        metrics.put("averageCostPerOrder", workOrderMetricsRepository.getAverageCostPerOrder(tenantId));
        
        // Productividad por técnico
        metrics.put("technicianProductivity", technicianMetricsRepository.getTechnicianProductivityMetrics(tenantId));
        
        return metrics;
    }
    
    public List<Object[]> getMonthlyTrends(String tenantId) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
        LocalDateTime endDate = LocalDateTime.now();
        
        return workOrderMetricsRepository.getMonthlyTrends(tenantId, startDate, endDate);
    }
    
    // === ANÁLISIS PREDICTIVO ===
    
    public Map<String, Object> getPredictiveAnalytics(String tenantId) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Predicción de carga de trabajo
        LocalDateTime forecastStart = LocalDateTime.now().minusDays(30);
        analytics.put("workloadForecast", workOrderMetricsRepository.getWorkloadForecast(tenantId, forecastStart));
        
        // Máquinas con alto riesgo de falla
        analytics.put("highRiskMachines", workOrderMetricsRepository.getHighRiskMachines(tenantId));
        
        // Recomendaciones de mantenimiento preventivo
        analytics.put("preventiveMaintenanceRecommendations", 
                workOrderMetricsRepository.getPreventiveMaintenanceRecommendations(tenantId));
        
        // Análisis de patrones de falla
        analytics.put("failurePatterns", workOrderMetricsRepository.getFailurePatterns(tenantId));
        
        return analytics;
    }
    
    // === REPORTES BÁSICOS ===
    
    public Map<String, Object> getBasicReport(String tenantId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        // Métricas básicas
        report.put("totalCost", workOrderMetricsRepository.getTotalCostInPeriod(tenantId, start, end));
        report.put("averageCompletionTime", workOrderMetricsRepository.getAverageCompletionTimeInPeriod(tenantId, start, end));
        
        // Análisis por categoría
        report.put("ordersByCategory", workOrderMetricsRepository.getOrdersByCategoryInPeriod(tenantId, start, end));
        
        // Performance de técnicos
        report.put("technicianPerformance", technicianMetricsRepository.getTechnicianPerformanceInPeriod(tenantId, start, end));
        
        return report;
    }
    
    // === MÉTRICAS DE CALIDAD ===
    
    public Map<String, Object> getQualityMetrics(String tenantId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Métricas básicas disponibles
        metrics.put("averageResolutionTime", workOrderMetricsRepository.getAverageResolutionTime(tenantId));
        metrics.put("firstTimeFixRate", workOrderMetricsRepository.getFirstTimeFixRate(tenantId));
        metrics.put("averageCostPerOrder", workOrderMetricsRepository.getAverageCostPerOrder(tenantId));
        
        return metrics;
    }
} 