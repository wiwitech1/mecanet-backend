package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderSearchQueryServiceImpl {
    
    private final WorkOrderRepository workOrderRepository;
    
    public WorkOrderSearchQueryServiceImpl(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }
    
    // === BÚSQUEDA BÁSICA ===
    
    public List<WorkOrder> searchWorkOrders(String tenantId, String searchTerm) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        // Búsqueda básica por título
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getTenantId().getValue().equals(tenantIdLong))
                .filter(wo -> wo.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                             wo.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // === BÚSQUEDA POR ESTADO ===
    
    public List<WorkOrder> searchByStatus(String tenantId, WorkOrderStatus status) {
        Long tenantIdLong = Long.valueOf(tenantId);
        return workOrderRepository.findByStatusAndTenantIdValue(status, tenantIdLong);
    }
    
    // === BÚSQUEDA POR MÁQUINA ===
    
    public List<WorkOrder> searchByMachine(String tenantId, String machineId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        Long machineIdLong = Long.valueOf(machineId);
        
        return workOrderRepository.findByMachineIdValueAndTenantIdValue(machineIdLong, tenantIdLong);
    }
    
    // === BÚSQUEDA CON PAGINACIÓN ===
    
    public Page<WorkOrder> searchWithPagination(String tenantId, String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Simulación de búsqueda paginada
        List<WorkOrder> allResults = searchWorkOrders(tenantId, searchTerm);
        
        int start = page * size;
        int end = Math.min(start + size, allResults.size());
        List<WorkOrder> pageContent = allResults.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allResults.size());
    }
    
    // === BÚSQUEDA AVANZADA ===
    
    public List<WorkOrder> advancedSearch(String tenantId, Map<String, Object> criteria) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        List<WorkOrder> results = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getTenantId().getValue().equals(tenantIdLong))
                .collect(Collectors.toList());
        
        // Filtrar por criterios
        if (criteria.containsKey("status")) {
            WorkOrderStatus status = (WorkOrderStatus) criteria.get("status");
            results = results.stream()
                    .filter(wo -> wo.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        
        if (criteria.containsKey("machineId")) {
            String machineId = (String) criteria.get("machineId");
            Long machineIdLong = Long.valueOf(machineId);
            results = results.stream()
                    .filter(wo -> wo.getMachineId().getValue().equals(machineIdLong))
                    .collect(Collectors.toList());
        }
        
        if (criteria.containsKey("title")) {
            String title = (String) criteria.get("title");
            results = results.stream()
                    .filter(wo -> wo.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return results;
    }
    
    // === BÚSQUEDA POR FECHA ===
    
    public List<WorkOrder> searchByDateRange(String tenantId, LocalDate startDate, LocalDate endDate) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getTenantId().getValue().equals(tenantIdLong))
                .filter(wo -> {
                    Date createdAt = wo.getCreatedAt();
                    return createdAt.after(java.sql.Timestamp.valueOf(start)) && 
                           createdAt.before(java.sql.Timestamp.valueOf(end));
                })
                .collect(Collectors.toList());
    }
    
    // === BÚSQUEDA DE ÓRDENES ATRASADAS ===
    
    public List<WorkOrder> searchDelayedOrders(String tenantId) {
        Long tenantIdLong = Long.valueOf(tenantId);
        return workOrderRepository.findDelayedWorkOrders(tenantIdLong);
    }
    
    // === AUTOCOMPLETADO ===
    
    public List<String> getAutocompleteSuggestions(String tenantId, String query) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getTenantId().getValue().equals(tenantIdLong))
                .map(WorkOrder::getTitle)
                .filter(title -> title.toLowerCase().contains(query.toLowerCase()))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }
    
    // === BÚSQUEDA POR MÚLTIPLES CRITERIOS ===
    
    public List<WorkOrder> searchMultipleCriteria(String tenantId, 
                                                 List<WorkOrderStatus> statuses,
                                                 List<String> machineIds,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Long tenantIdLong = Long.valueOf(tenantId);
        
        List<WorkOrder> results = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getTenantId().getValue().equals(tenantIdLong))
                .collect(Collectors.toList());
        
        // Filtrar por estados
        if (statuses != null && !statuses.isEmpty()) {
            results = results.stream()
                    .filter(wo -> statuses.contains(wo.getStatus()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por máquinas
        if (machineIds != null && !machineIds.isEmpty()) {
            Set<Long> machineIdLongs = machineIds.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            results = results.stream()
                    .filter(wo -> machineIdLongs.contains(wo.getMachineId().getValue()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por fechas
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            results = results.stream()
                    .filter(wo -> {
                        Date createdAt = wo.getCreatedAt();
                        return createdAt.after(java.sql.Timestamp.valueOf(start)) && 
                               createdAt.before(java.sql.Timestamp.valueOf(end));
                    })
                    .collect(Collectors.toList());
        }
        
        return results;
    }
    
    // === EXPORTACIÓN DE RESULTADOS ===
    
    public List<Map<String, Object>> exportSearchResults(String tenantId, Map<String, Object> criteria) {
        List<WorkOrder> workOrders = advancedSearch(tenantId, criteria);
        
        return workOrders.stream()
                .map(wo -> {
                    Map<String, Object> exportData = new HashMap<>();
                    exportData.put("workOrderId", wo.getWorkOrderId().getValue());
                    exportData.put("title", wo.getTitle());
                    exportData.put("description", wo.getDescription());
                    exportData.put("status", wo.getStatus().toString());
                    exportData.put("machineId", wo.getMachineId().getValue());
                    exportData.put("createdAt", wo.getCreatedAt());
                    exportData.put("updatedAt", wo.getUpdatedAt());
                    return exportData;
                })
                .collect(Collectors.toList());
    }
} 