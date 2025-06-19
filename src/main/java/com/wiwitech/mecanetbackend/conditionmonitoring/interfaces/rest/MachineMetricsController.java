package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries.*;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MachineMetricsCommandService;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MachineMetricsQueryService;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MetricQueryService;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources.*;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * MachineMetricsController
 * This controller handles machine metrics management operations with multitenancy support
 */
@RestController
@RequestMapping(value = "/api/v1/machines/{machineId}/metrics",
                produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Machine Metrics", description = "Machine Metrics Endpoints")
public class MachineMetricsController {

    private final MachineMetricsCommandService commandService;
    private final MachineMetricsQueryService   queryService;
    private final MetricQueryService           metricQueryService;

    public MachineMetricsController(MachineMetricsCommandService commandService,
                                    MachineMetricsQueryService queryService,
                                    MetricQueryService metricQueryService) {
        this.commandService   = commandService;
        this.queryService     = queryService;
        this.metricQueryService = metricQueryService;
    }

    /* ---------- COMMAND ---------- */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary     = "Record metric reading",
        description = "Registers a new measurement (value and timestamp) for the specified machine and metric."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Metric recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> recordMetric(@PathVariable Long machineId,
                                          @RequestBody RecordMetricResource body) {
        var cmd = RecordMetricCommandFromResourceAssembler.toCommand(machineId, body);
        commandService.handle(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* ---------- QUERIES ---------- */

    @GetMapping
    @Operation(
        summary     = "Get current metrics",
        description = "Returns the latest value of every metric currently tracked for the specified machine."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current metrics retrieved successfully")
    })
    public ResponseEntity<List<CurrentMetricResource>> getCurrentMetrics(@PathVariable Long machineId) {

        var currentMap = queryService.handle(new GetCurrentMetricsByMachineQuery(machineId));
        if (currentMap.isEmpty()) return ResponseEntity.ok(List.of());

        // Obtener catálogo para enriquecer
        var defs = metricQueryService.handle(new GetAllMetricDefinitionsQuery())
                                     .stream().collect(java.util.stream.Collectors.toMap(
                                             d -> d.getId(), d -> d));

        var list = currentMap.entrySet().stream()
                .map(e -> CurrentMetricResourceAssembler.toResource(
                        e.getKey(),
                        (com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.valueobjects.CurrentMetric) e.getValue(),
                        defs.get(e.getKey())))
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{metricId}")
    @Operation(
        summary     = "Get current metric value",
        description = "Returns the latest reading of a specific metric for the specified machine."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current metric retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Metric not found for this machine")
    })
    public ResponseEntity<CurrentMetricResource> getCurrentMetric(@PathVariable Long machineId,
                                                                  @PathVariable Long metricId) {

        var opt = queryService.handle(new GetCurrentMetricQuery(machineId, metricId));
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var def = metricQueryService.handle(new GetAllMetricDefinitionsQuery())
                                    .stream()
                                    .filter(d -> d.getId().equals(metricId))
                                    .findFirst()
                                    .orElseThrow();

        var res = CurrentMetricResourceAssembler.toResource(
                metricId,
                (com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.valueobjects.CurrentMetric) opt.get(),
                def);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{metricId}/readings")
    @Operation(
        summary     = "Get metric readings (historical)",
        description = "Returns a paginated list of historical readings for the specified metric and machine "
                    + "within an optional time range."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metric readings retrieved successfully")
    })
    public ResponseEntity<Page<MetricReadingResource>> getReadings(
            @PathVariable Long machineId,
            @PathVariable Long metricId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        from = from != null ? from : Instant.EPOCH;
        to   = to   != null ? to   : Instant.now();

        var pageRes = queryService.handle(
                new GetMetricReadingsQuery(machineId, metricId, from, to, page, size))
                .map(r -> MetricReadingResourceFromEntityAssembler
                        .toResource((com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.entities.MetricReading) r));

        return ResponseEntity.ok(pageRes);
    }
}
