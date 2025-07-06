package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.DeactivatePlanCommand;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries.GetAllDynamicPlansQuery;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries.GetMaintenancePlanByIdQuery;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.services.MaintenancePlanCommandService;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.services.MaintenancePlanQueryService;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.transform.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de planes de mantenimiento dinámicos
 */
@RestController
@RequestMapping(value = "/api/v1/maintenance-plans/dynamic", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dynamic Maintenance Plans", description = "Gestión de planes de mantenimiento dinámicos")
public class DynamicPlansController {

    private final MaintenancePlanCommandService commandService;
    private final MaintenancePlanQueryService queryService;

    public DynamicPlansController(MaintenancePlanCommandService commandService,
                                MaintenancePlanQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear plan dinámico", 
               description = "Crea un nuevo plan de mantenimiento dinámico basado en métricas")
    @ApiResponse(responseCode = "201", description = "Plan creado exitosamente")
    public ResponseEntity<DynamicPlanResource> createPlan(@Valid @RequestBody CreateDynamicPlanResource resource) {
        var command = CreateDynamicPlanCommandFromResourceAssembler.toCommand(resource);
        var plan = commandService.handle(command);
        var planResource = DynamicPlanResourceFromEntityAssembler.toResource(plan);
        return new ResponseEntity<>(planResource, HttpStatus.CREATED);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Obtener plan dinámico", 
               description = "Obtiene un plan dinámico por su ID")
    @ApiResponse(responseCode = "200", description = "Plan encontrado")
    @ApiResponse(responseCode = "404", description = "Plan no encontrado")
    public ResponseEntity<DynamicPlanResource> getPlan(@PathVariable Long planId) {
        return queryService.handle(new GetMaintenancePlanByIdQuery(planId))
                .filter(plan -> plan instanceof com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan)
                .map(plan -> (com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan) plan)
                .map(DynamicPlanResourceFromEntityAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{planId}/tasks")
    @Operation(summary = "Añadir tarea al plan", 
               description = "Añade una nueva tarea al plan dinámico")
    @ApiResponse(responseCode = "201", description = "Tarea añadida exitosamente")
    public ResponseEntity<DynamicTaskResource> addTask(@PathVariable Long planId,
                                                     @Valid @RequestBody AddTaskToDynamicPlanResource resource) {
        var command = AddTaskToDynamicPlanCommandFromResourceAssembler.toCommand(planId, resource);
        var task = commandService.handle(command);
        var taskResource = DynamicTaskResourceFromEntityAssembler.toResource(task);
        return new ResponseEntity<>(taskResource, HttpStatus.CREATED);
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "Desactivar plan", 
               description = "Desactiva el plan dinámico")
    @ApiResponse(responseCode = "200", description = "Plan desactivado exitosamente")
    public ResponseEntity<Void> deactivatePlan(@PathVariable Long planId) {
        commandService.handle(new DeactivatePlanCommand(planId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Listar planes dinámicos",
               description = "Devuelve todos los planes dinámicos del tenant actual")
    public ResponseEntity<List<DynamicPlanResource>> getAll() {
        var resources = queryService.handle(new GetAllDynamicPlansQuery())
                                    .stream()
                                    .map(DynamicPlanResourceFromEntityAssembler::toResource)
                                    .toList();
        return ResponseEntity.ok(resources);
    }
}
