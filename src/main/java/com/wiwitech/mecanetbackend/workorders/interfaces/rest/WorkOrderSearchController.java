package com.wiwitech.mecanetbackend.workorders.interfaces.rest;

import com.wiwitech.mecanetbackend.workorders.application.internal.queryservices.WorkOrderSearchQueryServiceImpl;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.WorkOrderResponseResource;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform.WorkOrderResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workorders/search")
@RequiredArgsConstructor
@Validated
@Tag(name = "WorkOrder Search", description = "Advanced Work Order Search Endpoints")
public class WorkOrderSearchController {
    
    private final WorkOrderSearchQueryServiceImpl searchService;

    @GetMapping("/advanced")
    @Operation(summary = "Advanced search", description = "Search work orders with multiple criteria.")
    public ResponseEntity<List<WorkOrderResponseResource>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // This would use methods from WorkOrderSearchQueryServiceImpl
        // For now, returning empty list as placeholder
        List<WorkOrderResponseResource> results = List.of();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/by-text")
    @Operation(summary = "Text search", description = "Search work orders by text in title or description.")
    public ResponseEntity<List<WorkOrderResponseResource>> searchByText(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // This would use text search methods from WorkOrderSearchQueryServiceImpl
        List<WorkOrderResponseResource> results = List.of();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Search by date range", description = "Search work orders within a date range.")
    public ResponseEntity<List<WorkOrderResponseResource>> searchByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // This would use date range search methods from WorkOrderSearchQueryServiceImpl
        List<WorkOrderResponseResource> results = List.of();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/by-priority")
    @Operation(summary = "Search by priority", description = "Search work orders by priority level.")
    public ResponseEntity<List<WorkOrderResponseResource>> searchByPriority(
            @RequestParam String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // This would use priority search methods from WorkOrderSearchQueryServiceImpl
        List<WorkOrderResponseResource> results = List.of();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filters")
    @Operation(summary = "Get available filters", description = "Get all available filter options for search.")
    public ResponseEntity<Map<String, Object>> getAvailableFilters() {
        Map<String, Object> filters = Map.of(
            "statuses", WorkOrderStatus.values(),
            "priorities", List.of("LOW", "MEDIUM", "HIGH", "CRITICAL"),
            "machines", List.of(), // Would be populated from actual data
            "technicians", List.of(), // Would be populated from actual data
            "skills", List.of() // Would be populated from actual data
        );
        return ResponseEntity.ok(filters);
    }
} 