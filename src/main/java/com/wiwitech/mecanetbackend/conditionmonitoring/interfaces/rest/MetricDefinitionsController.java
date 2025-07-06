package com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries.GetAllMetricDefinitionsQuery;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MetricQueryService;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.resources.MetricDefinitionResource;
import com.wiwitech.mecanetbackend.conditionmonitoring.interfaces.rest.transform.MetricDefinitionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MetricDefinitionsController
 * Exposes the global metric catalog.
 */
@RestController
@RequestMapping(value = "/api/v1/metric-definitions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Metric Definitions", description = "Global metric catalog endpoints")
public class MetricDefinitionsController {

    private final MetricQueryService metricQueryService;

    public MetricDefinitionsController(MetricQueryService metricQueryService) {
        this.metricQueryService = metricQueryService;
    }

    @GetMapping
    @Operation(
        summary     = "Get all metric definitions",
        description = "Returns the complete global catalog of metric types (name, unit, id)."
    )
    @ApiResponse(responseCode = "200", description = "Metric definitions retrieved successfully")
    public ResponseEntity<List<MetricDefinitionResource>> getAll() {

        var defs = metricQueryService.handle(new GetAllMetricDefinitionsQuery());
        var resources = defs.stream()
                .map(MetricDefinitionResourceFromEntityAssembler::toResource)
                .toList();

        return ResponseEntity.ok(resources);
    }
}