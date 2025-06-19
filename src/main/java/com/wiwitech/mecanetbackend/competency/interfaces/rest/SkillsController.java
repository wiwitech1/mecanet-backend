package com.wiwitech.mecanetbackend.competency.interfaces.rest;

import com.wiwitech.mecanetbackend.competency.domain.model.aggregates.Skill;
import com.wiwitech.mecanetbackend.competency.domain.services.SkillCommandService;
import com.wiwitech.mecanetbackend.competency.domain.services.SkillQueryService;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.resources.CreateSkillResource;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.resources.UpdateSkillResource;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.transform.CreateSkillCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.transform.UpdateSkillCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.resources.SkillResource;
import com.wiwitech.mecanetbackend.competency.interfaces.rest.transform.SkillResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.competency.domain.model.queries.GetAllSkillsQuery;
import com.wiwitech.mecanetbackend.competency.domain.model.queries.GetSkillByIdQuery;
import com.wiwitech.mecanetbackend.competency.domain.model.commands.DeactivateSkillCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SkillsController
 * Exposes CRUD endpoints for the skill catalog with multitenancy support.
 */
@RestController
@RequestMapping(value = "/api/v1/skills", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Skills", description = "Skill catalog endpoints")
public class SkillsController {

    private final SkillCommandService commandService;
    private final SkillQueryService   queryService;

    public SkillsController(SkillCommandService commandService,
                            SkillQueryService queryService) {
        this.commandService = commandService;
        this.queryService   = queryService;
    }

    /* ---------- CREATE ---------- */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create skill",
               description = "Registers a new skill in the current tenant.")
    @ApiResponse(responseCode = "201", description = "Skill created successfully")
    public ResponseEntity<SkillResource> create(@RequestBody CreateSkillResource body) {
        var skill = commandService.handle(
                CreateSkillCommandFromResourceAssembler.toCommand(body));
        return new ResponseEntity<>(SkillResourceFromEntityAssembler.toResource(skill),
                                    HttpStatus.CREATED);
    }

    /* ---------- READ ALL ---------- */
    @GetMapping
    @Operation(summary = "Get all skills",
               description = "Returns every skill defined for the current tenant.")
    @ApiResponse(responseCode = "200", description = "Skills retrieved successfully")
    public ResponseEntity<List<SkillResource>> getAll() {
        var resources = queryService.handle(new GetAllSkillsQuery())
                                    .stream()
                                    .map(SkillResourceFromEntityAssembler::toResource)
                                    .toList();
        return ResponseEntity.ok(resources);
    }

    /* ---------- READ ONE ---------- */
    @GetMapping("/{skillId}")
    @Operation(summary = "Get skill by id",
               description = "Returns a single skill by its identifier.")
    @ApiResponse(responseCode = "200", description = "Skill retrieved successfully")
    public ResponseEntity<SkillResource> getById(@PathVariable Long skillId) {
        return queryService.handle(new GetSkillByIdQuery(skillId))
                .map(SkillResourceFromEntityAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ---------- UPDATE ---------- */
    @PutMapping("/{skillId}")
    @Operation(summary = "Update skill",
               description = "Updates name, description or category of a skill.")
    @ApiResponse(responseCode = "200", description = "Skill updated successfully")
    public ResponseEntity<SkillResource> update(@PathVariable Long skillId,
                                                @RequestBody UpdateSkillResource body) {
        var skill = commandService.handle(
                UpdateSkillCommandFromResourceAssembler.toCommand(skillId, body));
        return ResponseEntity.ok(SkillResourceFromEntityAssembler.toResource(skill));
    }

    /* ---------- DEACTIVATE ---------- */
    @DeleteMapping("/{skillId}")
    @Operation(summary = "Deactivate skill",
               description = "Marks the skill as inactive. It cannot be removed if referenced.")
    @ApiResponse(responseCode = "200", description = "Skill deactivated successfully")
    public ResponseEntity<Void> deactivate(@PathVariable Long skillId) {
        commandService.handle(new DeactivateSkillCommand(skillId));
        return ResponseEntity.ok().build();
    }
}