package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries.GetAllStaticPlansQuery;
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

import java.util.Collections;
import java.util.List;

/**
 * Controller para gestión de planes de mantenimiento estáticos
 */
@RestController
@RequestMapping(value = "/api/v1/maintenance-plans/static", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Static Maintenance Plans", description = "Gestión de planes de mantenimiento estáticos")
public class StaticPlansController {

    private final MaintenancePlanCommandService commandService;
    private final MaintenancePlanQueryService queryService;

    public StaticPlansController(MaintenancePlanCommandService commandService,
                               MaintenancePlanQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear plan estático", 
               description = "Crea un nuevo plan de mantenimiento estático basado en calendario")
    @ApiResponse(responseCode = "201", description = "Plan creado exitosamente")
    public ResponseEntity<StaticPlanResource> createPlan(@Valid @RequestBody CreateStaticPlanResource resource) {
        var command = CreateStaticPlanCommandFromResourceAssembler.toCommand(resource);
        var plan = commandService.handle(command);
        var planResource = StaticPlanResourceFromEntityAssembler.toResource(plan);
        return new ResponseEntity<>(planResource, HttpStatus.CREATED);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Obtener plan estático", 
               description = "Obtiene un plan estático por su ID")
    @ApiResponse(responseCode = "200", description = "Plan encontrado")
    @ApiResponse(responseCode = "404", description = "Plan no encontrado")
    public ResponseEntity<StaticPlanResource> getPlan(@PathVariable Long planId) {
        return queryService.handle(new GetMaintenancePlanByIdQuery(planId))
                .filter(plan -> plan instanceof com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan)
                .map(plan -> (com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan) plan)
                .map(StaticPlanResourceFromEntityAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{planId}/items")
    @Operation(summary = "Añadir ítem al plan", 
               description = "Añade un nuevo ítem (día) al plan estático")
    @ApiResponse(responseCode = "201", description = "Ítem añadido exitosamente")
    public ResponseEntity<StaticPlanItemResource> addItem(@PathVariable Long planId,
                                                        @Valid @RequestBody AddItemToStaticPlanResource resource) {
        var command = new AddItemToStaticPlanCommand(planId, resource.dayIndex());
        var item = commandService.handle(command);
        var itemResource = StaticPlanItemResourceFromEntityAssembler.toResource(item);
        return new ResponseEntity<>(itemResource, HttpStatus.CREATED);
    }

    @PostMapping("/{planId}/items/{itemId}/tasks")
    @Operation(summary = "Añadir tarea al ítem", 
               description = "Añade una nueva tarea a un ítem específico del plan")
    @ApiResponse(responseCode = "201", description = "Tarea añadida exitosamente")
    public ResponseEntity<StaticTaskResource> addTaskToItem(@PathVariable Long planId,
                                                          @PathVariable Long itemId,
                                                          @Valid @RequestBody AddTaskToStaticItemResource resource) {
        var command = new AddTaskToStaticItemCommand(
                planId,
                itemId,
                resource.machineId(),
                resource.taskName(),
                resource.description(),
                resource.skillIds() != null ? resource.skillIds() : Collections.emptySet()
        );
        var task = commandService.handle(command);
        var taskResource = StaticTaskResourceFromEntityAssembler.toResource(task);
        return new ResponseEntity<>(taskResource, HttpStatus.CREATED);
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "Desactivar plan", 
               description = "Desactiva el plan estático")
    @ApiResponse(responseCode = "200", description = "Plan desactivado exitosamente")
    public ResponseEntity<Void> deactivatePlan(@PathVariable Long planId) {
        commandService.handle(new DeactivatePlanCommand(planId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Listar planes estáticos",
               description = "Devuelve todos los planes estáticos del tenant actual")
    public ResponseEntity<List<StaticPlanResource>> getAll() {
        var resources = queryService.handle(new GetAllStaticPlansQuery())
                                    .stream()
                                    .map(StaticPlanResourceFromEntityAssembler::toResource)
                                    .toList();
        return ResponseEntity.ok(resources);
    }
}