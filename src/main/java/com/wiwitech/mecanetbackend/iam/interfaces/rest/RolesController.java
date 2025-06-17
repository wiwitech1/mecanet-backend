package com.wiwitech.mecanetbackend.iam.interfaces.rest;

import com.wiwitech.mecanetbackend.iam.domain.model.queries.GetAllRolesQuery;
import com.wiwitech.mecanetbackend.iam.domain.services.RoleQueryService;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.resources.RoleResource;
import com.wiwitech.mecanetbackend.iam.interfaces.rest.transform.RoleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  Roles Controller
 *  This controller is responsible for handling all the requests related to roles
 */
@RestController
@RequestMapping(value = "/ap/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles", description = "Available Role Endpoints")
public class RolesController {
    private final RoleQueryService roleQueryService;

    public RolesController(RoleQueryService roleQueryService) {
        this.roleQueryService = roleQueryService;
    }

    /**
     * Get all roles
     * @return List of role resources
     * @see RoleResource
     */
    @GetMapping
    @Operation(summary = "Get all roles", description = "Get all the roles available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")})
    public ResponseEntity<List<RoleResource>> getAllRoles() {
        var getAllRolesQuery = new GetAllRolesQuery();
        var roles = roleQueryService.handle(getAllRolesQuery);
        var roleResources = roles.stream().map(RoleResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(roleResources);
    }
}

