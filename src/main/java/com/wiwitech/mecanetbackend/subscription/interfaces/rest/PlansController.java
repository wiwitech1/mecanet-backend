package com.wiwitech.mecanetbackend.subscription.interfaces.rest;

import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetAllActivePlansQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanByIdQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanAttributesQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.DeactivatePlanCommand;
import com.wiwitech.mecanetbackend.subscription.domain.services.PlanCommandService;
import com.wiwitech.mecanetbackend.subscription.domain.services.PlanQueryService;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.CreatePlanResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.PlanAttributeResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.PlanResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.UpdatePlanResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.CreatePlanCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.PlanAttributeResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.UpdatePlanCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PlansController
 * Handles plan management operations (public endpoints + admin endpoints)
 */
@RestController
@RequestMapping(value = "/api/v1/plans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plans", description = "Subscription Plans Management")
public class PlansController {

    private static final Logger LOG = LoggerFactory.getLogger(PlansController.class);
    
    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlansController(PlanCommandService planCommandService, PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    /* ---------- PUBLIC ENDPOINTS ---------- */

    /**
     * Get all active plans
     */
    @GetMapping
    @Operation(summary = "Get all active plans", description = "Returns all active subscription plans")
    @ApiResponse(responseCode = "200", description = "Plans retrieved successfully")
    public ResponseEntity<List<PlanResource>> getAllActivePlans() {
        LOG.info("Getting all active plans");
        
        var plans = planQueryService.handle(new GetAllActivePlansQuery());
        var planResources = plans.stream()
                .map(PlanResourceFromEntityAssembler::toResource)
                .toList();
        
        LOG.info("Retrieved {} active plans", planResources.size());
        return ResponseEntity.ok(planResources);
    }

    /**
     * Get plan by ID
     */
    @GetMapping("/{planId}")
    @Operation(summary = "Get plan by ID", description = "Returns a specific plan by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<PlanResource> getPlanById(@PathVariable Long planId) {
        LOG.info("Getting plan by ID: {}", planId);
        
        return planQueryService.handle(new GetPlanByIdQuery(planId))
                .map(PlanResourceFromEntityAssembler::toResource)
                .map(planResource -> {
                    LOG.info("Plan retrieved successfully: {}", planResource.name());
                    return ResponseEntity.ok(planResource);
                })
                .orElseGet(() -> {
                    LOG.warn("Plan not found with ID: {}", planId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Get plan attributes
     */
    @GetMapping("/{planId}/attributes")
    @Operation(summary = "Get plan attributes", description = "Returns all attributes for a specific plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan attributes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<List<PlanAttributeResource>> getPlanAttributes(@PathVariable Long planId) {
        LOG.info("Getting attributes for plan ID: {}", planId);
        
        try {
            var attributes = planQueryService.handle(new GetPlanAttributesQuery(planId));
            var attributeResources = attributes.stream()
                    .map(PlanAttributeResourceFromEntityAssembler::toResource)
                    .toList();
            
            LOG.info("Retrieved {} attributes for plan ID: {}", attributeResources.size(), planId);
            return ResponseEntity.ok(attributeResources);
        } catch (IllegalArgumentException e) {
            LOG.warn("Plan not found with ID: {}", planId);
            return ResponseEntity.notFound().build();
        }
    }

    /* ---------- ADMIN ENDPOINTS ---------- */

    /**
     * Create a new plan (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create plan", description = "Create a new subscription plan (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid plan data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<PlanResource> createPlan(@Valid @RequestBody CreatePlanResource createPlanResource) {
        LOG.info("Creating new plan: {}", createPlanResource.name());
        
        var command = CreatePlanCommandFromResourceAssembler.toCommand(createPlanResource);
        var plan = planCommandService.handle(command);
        var planResource = PlanResourceFromEntityAssembler.toResource(plan);
        
        LOG.info("Plan created successfully: {}", planResource.name());
        return new ResponseEntity<>(planResource, HttpStatus.CREATED);
    }

    /**
     * Update an existing plan (Admin only)
     */
    @PutMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update plan", description = "Update an existing subscription plan (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid plan data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<PlanResource> updatePlan(@PathVariable Long planId, 
                                                  @Valid @RequestBody UpdatePlanResource updatePlanResource) {
        LOG.info("Updating plan with ID: {}", planId);
        
        try {
            var command = UpdatePlanCommandFromResourceAssembler.toCommand(planId, updatePlanResource);
            var plan = planCommandService.handle(command);
            var planResource = PlanResourceFromEntityAssembler.toResource(plan);
            
            LOG.info("Plan updated successfully: {}", planResource.name());
            return ResponseEntity.ok(planResource);
        } catch (IllegalArgumentException e) {
            LOG.warn("Plan not found with ID: {}", planId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate a plan (Admin only)
     */
    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate plan", description = "Deactivate a subscription plan (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<PlanResource> deactivatePlan(@PathVariable Long planId) {
        LOG.info("Deactivating plan with ID: {}", planId);
        
        try {
            var command = new DeactivatePlanCommand(planId);
            var plan = planCommandService.handle(command);
            var planResource = PlanResourceFromEntityAssembler.toResource(plan);
            
            LOG.info("Plan deactivated successfully: {}", planResource.name());
            return ResponseEntity.ok(planResource);
        } catch (IllegalArgumentException e) {
            LOG.warn("Plan not found with ID: {}", planId);
            return ResponseEntity.notFound().build();
        }
    }
} 