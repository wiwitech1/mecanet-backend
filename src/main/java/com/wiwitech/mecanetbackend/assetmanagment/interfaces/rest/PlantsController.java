// PlantsController.java
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

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.ActivatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CreatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.DeactivatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.UpdatePlantCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetActivePlantsQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAllPlantsQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetPlantByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.PlantCommandService;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.PlantQueryService;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.CreatePlantResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.PlantResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.UpdatePlantResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.CreatePlantCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.PlantResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.UpdatePlantCommandFromResourceAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * PlantsController
 * This controller handles plant management operations with multitenancy support
 */
@RestController
@RequestMapping(value = "/api/v1/plants", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plants", description = "Plant Management Endpoints")
public class PlantsController {

    private static final Logger logger = LoggerFactory.getLogger(PlantsController.class);
    
    private final PlantQueryService plantQueryService;
    private final PlantCommandService plantCommandService;

    public PlantsController(PlantQueryService plantQueryService, PlantCommandService plantCommandService) {
        this.plantQueryService = plantQueryService;
        this.plantCommandService = plantCommandService;
    }

    /**
     * Get all plants for the current tenant
     */
    @GetMapping
    @Operation(summary = "Get all plants", description = "Get all plants for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plants retrieved successfully")})
    public ResponseEntity<List<PlantResource>> getAllPlants() {
        logger.info("Getting all plants for current tenant");
        
        var getAllPlantsQuery = new GetAllPlantsQuery();
        var plants = plantQueryService.handle(getAllPlantsQuery);
        var plantResources = plants.stream()
                .map(PlantResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} plants", plantResources.size());
        return ResponseEntity.ok(plantResources);
    }

    /**
     * Get active plants for the current tenant
     */
    @GetMapping("/active")
    @Operation(summary = "Get active plants", description = "Get all active plants for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active plants retrieved successfully")})
    public ResponseEntity<List<PlantResource>> getActivePlants() {
        logger.info("Getting active plants for current tenant");
        
        var getActivePlantsQuery = new GetActivePlantsQuery();
        var plants = plantQueryService.handle(getActivePlantsQuery);
        var plantResources = plants.stream()
                .map(PlantResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} active plants", plantResources.size());
        return ResponseEntity.ok(plantResources);
    }

    /**
     * Get plant by ID
     */
    @GetMapping("/{plantId}")
    @Operation(summary = "Get plant by ID", description = "Get plant by ID for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found")})
    public ResponseEntity<PlantResource> getPlantById(@PathVariable Long plantId) {
        logger.info("Getting plant by ID: {}", plantId);
        
        var getPlantByIdQuery = new GetPlantByIdQuery(plantId);
        var plantOptional = plantQueryService.handle(getPlantByIdQuery);
        
        if (plantOptional.isEmpty()) {
            logger.warn("Plant not found with ID: {}", plantId);
            throw new RuntimeException("Plant not found");
        }
        
        var plantResource = PlantResourceFromEntityAssembler.toResourceFromEntity(plantOptional.get());
        logger.info("Plant retrieved successfully: {}", plantResource.name());
        return ResponseEntity.ok(plantResource);
    }
    
    /**
     * Create a new plant
     */
    @PostMapping
    @Operation(summary = "Create plant", description = "Create a new plant in the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<PlantResource> createPlant(@RequestBody CreatePlantResource createPlantResource) {
        logger.info("Creating new plant: {}", createPlantResource.name());
        
        CreatePlantCommand command = CreatePlantCommandFromResourceAssembler.toCommandFromResource(createPlantResource);
        var plant = plantCommandService.handle(command);
        var plantResource = PlantResourceFromEntityAssembler.toResourceFromEntity(plant);
        
        logger.info("Plant created successfully: {}", plantResource.name());
        return new ResponseEntity<>(plantResource, HttpStatus.CREATED);
    }
    
    /**
     * Update an existing plant
     */
    @PutMapping("/{plantId}")
    @Operation(summary = "Update plant", description = "Update an existing plant in the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant updated successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<PlantResource> updatePlant(@PathVariable Long plantId, 
                                                    @RequestBody UpdatePlantResource updatePlantResource) {
        logger.info("Updating plant with ID: {}", plantId);
        
        UpdatePlantCommand command = UpdatePlantCommandFromResourceAssembler.toCommandFromResource(plantId, updatePlantResource);
        var plant = plantCommandService.handle(command);
        var plantResource = PlantResourceFromEntityAssembler.toResourceFromEntity(plant);
        
        logger.info("Plant updated successfully: {}", plantResource.name());
        return ResponseEntity.ok(plantResource);
    }
    
    /**
     * Activate a plant
     */
    @PutMapping("/{plantId}/activate")
    @Operation(summary = "Activate plant", description = "Activate a plant in the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant activated successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found")})
    public ResponseEntity<PlantResource> activatePlant(@PathVariable Long plantId) {
        logger.info("Activating plant with ID: {}", plantId);
        
        ActivatePlantCommand command = new ActivatePlantCommand(plantId);
        var plant = plantCommandService.handle(command);
        var plantResource = PlantResourceFromEntityAssembler.toResourceFromEntity(plant);
        
        logger.info("Plant activated successfully: {}", plantResource.name());
        return ResponseEntity.ok(plantResource);
    }
    
    /**
     * Deactivate a plant
     */
    @PutMapping("/{plantId}/deactivate")
    @Operation(summary = "Deactivate plant", description = "Deactivate a plant in the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found"),
            @ApiResponse(responseCode = "400", description = "Cannot deactivate plant with active production lines")})
    public ResponseEntity<PlantResource> deactivatePlant(@PathVariable Long plantId) {
        logger.info("Deactivating plant with ID: {}", plantId);
        
        DeactivatePlantCommand command = new DeactivatePlantCommand(plantId);
        var plant = plantCommandService.handle(command);
        var plantResource = PlantResourceFromEntityAssembler.toResourceFromEntity(plant);
        
        logger.info("Plant deactivated successfully: {}", plantResource.name());
        return ResponseEntity.ok(plantResource);
    }
}