package com.wiwitech.mecanetbackend.workorders.interfaces.rest;

import com.wiwitech.mecanetbackend.workorders.domain.services.TechnicianCommandService;
import com.wiwitech.mecanetbackend.workorders.domain.services.TechnicianQueryService;
import com.wiwitech.mecanetbackend.workorders.domain.model.queries.*;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.CreateTechnicianCommand;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.TechnicianRequestResource;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.TechnicianResponseResource;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform.CreateTechnicianCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform.TechnicianResourceFromEntityAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/technicians")
@RequiredArgsConstructor
@Validated
@Tag(name = "Technicians", description = "Technician Management Endpoints")
public class TechniciansController {
    private final TechnicianCommandService commandService;
    private final TechnicianQueryService queryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get technician by ID", description = "Retrieve a technician by its unique identifier.")
    public ResponseEntity<TechnicianResponseResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetTechnicianByIdQuery(new TechnicianId(id)))
            .map(TechnicianResourceFromEntityAssembler::toResource)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get technicians by status", description = "Retrieve all technicians filtered by status. If no status is provided, returns all technicians.")
    public List<TechnicianResponseResource> getByStatus(@RequestParam(required = false) TechnicianStatus status) {
        List<Technician> list = (status != null)
            ? queryService.handle(new GetTechniciansByStatusQuery(status))
            : queryService.handle(new GetTechniciansByStatusQuery(null));
        return list.stream().map(TechnicianResourceFromEntityAssembler::toResource).toList();
    }

    @GetMapping("/by-iam-user/{userId}")
    @Operation(summary = "Get technician by IAM user ID", description = "Retrieve a technician by its IAM user identifier.")
    public ResponseEntity<TechnicianResponseResource> getByIamUserId(@PathVariable Long userId) {
        return queryService.handle(new GetTechnicianByIamUserIdQuery(new UserId(userId)))
            .map(TechnicianResourceFromEntityAssembler::toResource)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-skill/{skillId}")
    @Operation(summary = "Get technicians by skill", description = "Retrieve all technicians that have a specific skill.")
    public List<TechnicianResponseResource> getBySkill(@PathVariable Long skillId) {
        return queryService.handle(new GetTechniciansBySkillQuery(new com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId(skillId)))
            .stream().map(TechnicianResourceFromEntityAssembler::toResource).toList();
    }

    @PostMapping
    @Operation(summary = "Register technician", description = "Register a new technician.")
    public ResponseEntity<TechnicianResponseResource> create(@Valid @RequestBody TechnicianRequestResource resource) {
        var cmd = CreateTechnicianCommandFromResourceAssembler.toCommand(resource);
        Technician tech = commandService.handle(cmd);
        return ResponseEntity.ok(TechnicianResourceFromEntityAssembler.toResource(tech));
    }
} 