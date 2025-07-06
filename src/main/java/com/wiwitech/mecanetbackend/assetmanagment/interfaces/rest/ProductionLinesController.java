// ProductionLinesController.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreateProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StartProductionCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StopProductionCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetProductionLineByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetProductionLinesByPlantQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetRunningProductionLinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.ProductionLineCommandService;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.ProductionLineQueryService;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.CreateProductionLineResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.ProductionLineResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.StopProductionResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.CreateProductionLineCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.ProductionLineResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.StopProductionCommandFromResourceAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ProductionLinesController
 * This controller handles production line management operations with multitenancy support
 */
@RestController
@RequestMapping(value = "/api/v1/production-lines", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Production Lines", description = "Production Line Management Endpoints")
public class ProductionLinesController {

    private static final Logger logger = LoggerFactory.getLogger(ProductionLinesController.class);
    
    private final ProductionLineQueryService productionLineQueryService;
    private final ProductionLineCommandService productionLineCommandService;

    public ProductionLinesController(ProductionLineQueryService productionLineQueryService, 
                                   ProductionLineCommandService productionLineCommandService) {
        this.productionLineQueryService = productionLineQueryService;
        this.productionLineCommandService = productionLineCommandService;
    }

    /**
     * Get running production lines for the current tenant
     */
/*     @GetMapping("/running")
    @Operation(summary = "Get running production lines", description = "Get all running production lines for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Running production lines retrieved successfully")})
    public ResponseEntity<List<ProductionLineResource>> getRunningProductionLines() {
        logger.info("Getting running production lines for current tenant");
        
        var getRunningQuery = new GetRunningProductionLinesQuery();
        var productionLines = productionLineQueryService.handle(getRunningQuery);
        var productionLineResources = productionLines.stream()
                .map(ProductionLineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} running production lines", productionLineResources.size());
        return ResponseEntity.ok(productionLineResources);
    } */

    /**
     * Get production line by ID
     */
    @GetMapping("/{productionLineId}")
    @Operation(summary = "Get production line by ID", description = "Get production line by ID for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Production line retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Production line not found")})
    public ResponseEntity<ProductionLineResource> getProductionLineById(@PathVariable Long productionLineId) {
        logger.info("Getting production line by ID: {}", productionLineId);
        
        var getProductionLineQuery = new GetProductionLineByIdQuery(productionLineId);
        var productionLineOptional = productionLineQueryService.handle(getProductionLineQuery);
        
        if (productionLineOptional.isEmpty()) {
            logger.warn("Production line not found with ID: {}", productionLineId);
            throw new RuntimeException("Production line not found");
        }
        
        var productionLineResource = ProductionLineResourceFromEntityAssembler.toResourceFromEntity(productionLineOptional.get());
        logger.info("Production line retrieved successfully: {}", productionLineResource.name());
        return ResponseEntity.ok(productionLineResource);
    }

    /**
     * Get production lines by plant
     */
    @GetMapping("/plant/{plantId}")
    @Operation(summary = "Get production lines by plant", description = "Get all production lines for a specific plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Production lines retrieved successfully")})
    public ResponseEntity<List<ProductionLineResource>> getProductionLinesByPlant(@PathVariable Long plantId) {
        logger.info("Getting production lines by plant: {}", plantId);
        
        var getProductionLinesQuery = new GetProductionLinesByPlantQuery(plantId);
        var productionLines = productionLineQueryService.handle(getProductionLinesQuery);
        var productionLineResources = productionLines.stream()
                .map(ProductionLineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} production lines for plant {}", productionLineResources.size(), plantId);
        return ResponseEntity.ok(productionLineResources);
    }
    
    /**
     * Create a new production line
     */
    @PostMapping
    @Operation(summary = "Create production line", description = "Create a new production line in the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Production line created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<ProductionLineResource> createProductionLine(@RequestBody CreateProductionLineResource createProductionLineResource) {
        logger.info("Creating new production line: {}", createProductionLineResource.name());
        
        CreateProductionLineCommand command = CreateProductionLineCommandFromResourceAssembler.toCommandFromResource(createProductionLineResource);
        var productionLine = productionLineCommandService.handle(command);
        var productionLineResource = ProductionLineResourceFromEntityAssembler.toResourceFromEntity(productionLine);
        
        logger.info("Production line created successfully: {}", productionLineResource.name());
        return new ResponseEntity<>(productionLineResource, HttpStatus.CREATED);
    }
    
    /**
     * Start production on a production line
     */
/*     @PutMapping("/{productionLineId}/start")
    @Operation(summary = "Start production", description = "Start production on a production line")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Production started successfully"),
            @ApiResponse(responseCode = "404", description = "Production line not found"),
            @ApiResponse(responseCode = "400", description = "Cannot start production")})
    public ResponseEntity<ProductionLineResource> startProduction(@PathVariable Long productionLineId) {
        logger.info("Starting production on line {}", productionLineId);
        
        StartProductionCommand command = new StartProductionCommand(productionLineId);
        var productionLine = productionLineCommandService.handle(command);
        var productionLineResource = ProductionLineResourceFromEntityAssembler.toResourceFromEntity(productionLine);
        
        logger.info("Production started successfully on line: {}", productionLineResource.name());
        return ResponseEntity.ok(productionLineResource);
    } */
    
    /**
     * Stop production on a production line
     */
/*     @PutMapping("/{productionLineId}/stop")
    @Operation(summary = "Stop production", description = "Stop production on a production line")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Production stopped successfully"),
            @ApiResponse(responseCode = "404", description = "Production line not found")})
    public ResponseEntity<ProductionLineResource> stopProduction(@PathVariable Long productionLineId, 
                                                                @RequestBody StopProductionResource stopProductionResource) {
        logger.info("Stopping production on line {} with reason: {}", productionLineId, stopProductionResource.reason());
        
        StopProductionCommand command = StopProductionCommandFromResourceAssembler.toCommandFromResource(productionLineId, stopProductionResource);
        var productionLine = productionLineCommandService.handle(command);
        var productionLineResource = ProductionLineResourceFromEntityAssembler.toResourceFromEntity(productionLine);
        
        logger.info("Production stopped successfully on line: {}", productionLineResource.name());
        return ResponseEntity.ok(productionLineResource);
    } */
}