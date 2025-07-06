// MachinesController.java
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.AssignMachineToProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CompleteMachineMaintenanceCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.RegisterMachineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StartMachineMaintenanceCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAllMachinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAvailableMachinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachineByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachineBySerialNumberQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachinesByProductionLineQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachinesDueForMaintenanceQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.MachineCommandService;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.MachineQueryService;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.AssignMachineResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.MachineResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources.RegisterMachineResource;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.AssignMachineCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.MachineResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.transform.RegisterMachineCommandFromResourceAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * MachinesController
 * This controller handles machine management operations with multitenancy support
 */
@RestController
@RequestMapping(value = "/api/v1/machines", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Machines", description = "Machine Management Endpoints")
public class MachinesController {

    private static final Logger logger = LoggerFactory.getLogger(MachinesController.class);
    
    private final MachineQueryService machineQueryService;
    private final MachineCommandService machineCommandService;

    public MachinesController(MachineQueryService machineQueryService, MachineCommandService machineCommandService) {
        this.machineQueryService = machineQueryService;
        this.machineCommandService = machineCommandService;
    }

    /**
     * Get all machines for the current tenant
     */
    @GetMapping
    @Operation(summary = "Get all machines", description = "Get all machines for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machines retrieved successfully")})
    public ResponseEntity<List<MachineResource>> getAllMachines() {
        logger.info("Getting all machines for current tenant");
        
        var getAllMachinesQuery = new GetAllMachinesQuery();
        var machines = machineQueryService.handle(getAllMachinesQuery);
        var machineResources = machines.stream()
                .map(MachineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} machines", machineResources.size());
        return ResponseEntity.ok(machineResources);
    }

    /**
     * Get available machines for the current tenant
     */
    @GetMapping("/available")
    @Operation(summary = "Get available machines", description = "Get all available machines for assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available machines retrieved successfully")})
    public ResponseEntity<List<MachineResource>> getAvailableMachines() {
        logger.info("Getting available machines for current tenant");
        
        var getAvailableMachinesQuery = new GetAvailableMachinesQuery();
        var machines = machineQueryService.handle(getAvailableMachinesQuery);
        var machineResources = machines.stream()
                .map(MachineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} available machines", machineResources.size());
        return ResponseEntity.ok(machineResources);
    }

    /**
     * Get machines due for maintenance
     */
    @GetMapping("/maintenance-due")
    @Operation(summary = "Get machines due for maintenance", description = "Get all machines that need maintenance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machines due for maintenance retrieved successfully")})
    public ResponseEntity<List<MachineResource>> getMachinesDueForMaintenance() {
        logger.info("Getting machines due for maintenance for current tenant");
        
        var getMachinesDueQuery = new GetMachinesDueForMaintenanceQuery();
        var machines = machineQueryService.handle(getMachinesDueQuery);
        var machineResources = machines.stream()
                .map(MachineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} machines due for maintenance", machineResources.size());
        return ResponseEntity.ok(machineResources);
    }

    /**
     * Get machine by ID
     */
    @GetMapping("/{machineId}")
    @Operation(summary = "Get machine by ID", description = "Get machine by ID for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machine retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Machine not found")})
    public ResponseEntity<MachineResource> getMachineById(@PathVariable Long machineId) {
        logger.info("Getting machine by ID: {}", machineId);
        
        var getMachineByIdQuery = new GetMachineByIdQuery(machineId);
        var machineOptional = machineQueryService.handle(getMachineByIdQuery);
        
        if (machineOptional.isEmpty()) {
            logger.warn("Machine not found with ID: {}", machineId);
            throw new RuntimeException("Machine not found");
        }
        
        var machineResource = MachineResourceFromEntityAssembler.toResourceFromEntity(machineOptional.get());
        logger.info("Machine retrieved successfully: {}", machineResource.serialNumber());
        return ResponseEntity.ok(machineResource);
    }

    /**
     * Get machine by serial number
     */
    @GetMapping("/serial/{serialNumber}")
    @Operation(summary = "Get machine by serial number", description = "Get machine by serial number for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machine retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Machine not found")})
    public ResponseEntity<MachineResource> getMachineBySerialNumber(@PathVariable String serialNumber) {
        logger.info("Getting machine by serial number: {}", serialNumber);
        
        var getMachineQuery = new GetMachineBySerialNumberQuery(serialNumber);
        var machineOptional = machineQueryService.handle(getMachineQuery);
        
        if (machineOptional.isEmpty()) {
            logger.warn("Machine not found with serial number: {}", serialNumber);
            throw new RuntimeException("Machine not found");
        }
        
        var machineResource = MachineResourceFromEntityAssembler.toResourceFromEntity(machineOptional.get());
        logger.info("Machine retrieved successfully: {}", machineResource.serialNumber());
        return ResponseEntity.ok(machineResource);
    }

    /**
     * Get machines by production line
     */
    @GetMapping("/production-line/{productionLineId}")
    @Operation(summary = "Get machines by production line", description = "Get all machines assigned to a production line")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machines retrieved successfully")})
    public ResponseEntity<List<MachineResource>> getMachinesByProductionLine(@PathVariable Long productionLineId) {
        logger.info("Getting machines by production line: {}", productionLineId);
        
        var getMachinesQuery = new GetMachinesByProductionLineQuery(productionLineId);
        var machines = machineQueryService.handle(getMachinesQuery);
        var machineResources = machines.stream()
                .map(MachineResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        logger.info("Retrieved {} machines for production line {}", machineResources.size(), productionLineId);
        return ResponseEntity.ok(machineResources);
    }
    
    /**
     * Register a new machine
     */
    @PostMapping
    @Operation(summary = "Register machine", description = "Register a new machine in the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Machine registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<MachineResource> registerMachine(@RequestBody RegisterMachineResource registerMachineResource) {
        logger.info("Registering new machine: {}", registerMachineResource.serialNumber());
        
        RegisterMachineCommand command = RegisterMachineCommandFromResourceAssembler.toCommandFromResource(registerMachineResource);
        var machine = machineCommandService.handle(command);
        var machineResource = MachineResourceFromEntityAssembler.toResourceFromEntity(machine);
        
        logger.info("Machine registered successfully: {}", machineResource.serialNumber());
        return new ResponseEntity<>(machineResource, HttpStatus.CREATED);
    }
    
    /**
     * Assign machine to production line
     */
    @PutMapping("/{machineId}/assign")
    @Operation(summary = "Assign machine to production line", description = "Assign a machine to a production line")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machine assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Machine not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<MachineResource> assignMachine(@PathVariable Long machineId, 
                                                        @RequestBody AssignMachineResource assignMachineResource) {
        logger.info("Assigning machine {} to production line {}", machineId, assignMachineResource.productionLineId());
        
        AssignMachineToProductionLineCommand command = AssignMachineCommandFromResourceAssembler.toCommandFromResource(machineId, assignMachineResource);
        var machine = machineCommandService.handle(command);
        var machineResource = MachineResourceFromEntityAssembler.toResourceFromEntity(machine);
        
        logger.info("Machine assigned successfully: {}", machineResource.serialNumber());
        return ResponseEntity.ok(machineResource);
    }
    
    /**
     * Start machine maintenance
     */
   /*  @PutMapping("/{machineId}/maintenance/start")
    @Operation(summary = "Start machine maintenance", description = "Start maintenance on a machine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance started successfully"),
            @ApiResponse(responseCode = "404", description = "Machine not found")})
    public ResponseEntity<MachineResource> startMaintenance(@PathVariable Long machineId) {
        logger.info("Starting maintenance for machine {}", machineId);
        
        StartMachineMaintenanceCommand command = new StartMachineMaintenanceCommand(machineId);
        var machine = machineCommandService.handle(command);
        var machineResource = MachineResourceFromEntityAssembler.toResourceFromEntity(machine);
        
        logger.info("Maintenance started successfully for machine: {}", machineResource.serialNumber());
        return ResponseEntity.ok(machineResource);
    }
     */
    /**
     * Complete machine maintenance
     */
 /*    @PutMapping("/{machineId}/maintenance/complete")
    @Operation(summary = "Complete machine maintenance", description = "Complete maintenance on a machine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance completed successfully"),
            @ApiResponse(responseCode = "404", description = "Machine not found")})
    public ResponseEntity<MachineResource> completeMaintenance(@PathVariable Long machineId) {
        logger.info("Completing maintenance for machine {}", machineId);
        
        CompleteMachineMaintenanceCommand command = new CompleteMachineMaintenanceCommand(machineId);
        var machine = machineCommandService.handle(command);
        var machineResource = MachineResourceFromEntityAssembler.toResourceFromEntity(machine);
        
        logger.info("Maintenance completed successfully for machine: {}", machineResource.serialNumber());
        return ResponseEntity.ok(machineResource);
    } */
}