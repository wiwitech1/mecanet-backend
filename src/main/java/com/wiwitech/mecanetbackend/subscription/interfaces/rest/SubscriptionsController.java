package com.wiwitech.mecanetbackend.subscription.interfaces.rest;

import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetSubscriptionByTenantQuery;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionCommandService;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionQueryService;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.ChangeSubscriptionPlanResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.CreateSubscriptionResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources.SubscriptionResource;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.ChangeSubscriptionPlanCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.subscription.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
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
import org.springframework.web.bind.annotation.*;

/**
 * SubscriptionsController
 * Handles subscription management operations with multitenancy support
 */
@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Subscription Management")
public class SubscriptionsController {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionsController.class);
    
    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionsController(SubscriptionCommandService subscriptionCommandService, 
                                 SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    /**
     * Get current tenant's subscription
     */
    @GetMapping("/current")
    @Operation(summary = "Get current subscription", description = "Returns the current tenant's subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<SubscriptionResource> getCurrentSubscription() {
        Long tenantId = requireTenant();
        LOG.info("Getting subscription for tenant: {}", tenantId);
        
        return subscriptionQueryService.handle(new GetSubscriptionByTenantQuery(tenantId))
                .map(SubscriptionResourceFromEntityAssembler::toResource)
                .map(subscriptionResource -> {
                    LOG.info("Subscription retrieved successfully for tenant: {}", tenantId);
                    return ResponseEntity.ok(subscriptionResource);
                })
                .orElseGet(() -> {
                    LOG.warn("Subscription not found for tenant: {}", tenantId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Create a new subscription for current tenant
     */
    @PostMapping
    @Operation(summary = "Create subscription", description = "Create a new subscription for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid subscription data or tenant already has subscription"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<SubscriptionResource> createSubscription(@Valid @RequestBody CreateSubscriptionResource createSubscriptionResource) {
        Long tenantId = requireTenant();
        LOG.info("Creating subscription for tenant: {} with plan: {}", tenantId, createSubscriptionResource.planId());
        
        try {
            var command = CreateSubscriptionCommandFromResourceAssembler.toCommand(tenantId, createSubscriptionResource);
            var subscription = subscriptionCommandService.handle(command);
            var subscriptionResource = SubscriptionResourceFromEntityAssembler.toResource(subscription);
            
            LOG.info("Subscription created successfully for tenant: {}", tenantId);
            return new ResponseEntity<>(subscriptionResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to create subscription for tenant {}: {}", tenantId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Change subscription plan for current tenant
     */
    @PutMapping("/change-plan")
    @Operation(summary = "Change subscription plan", description = "Change the subscription plan for the current tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription plan changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid plan data or same plan selected"),
            @ApiResponse(responseCode = "404", description = "Subscription or plan not found")
    })
    public ResponseEntity<SubscriptionResource> changeSubscriptionPlan(@Valid @RequestBody ChangeSubscriptionPlanResource changeSubscriptionPlanResource) {
        Long tenantId = requireTenant();
        LOG.info("Changing subscription plan for tenant: {} to plan: {}", tenantId, changeSubscriptionPlanResource.newPlanId());
        
        try {
            var command = ChangeSubscriptionPlanCommandFromResourceAssembler.toCommand(tenantId, changeSubscriptionPlanResource);
            var subscription = subscriptionCommandService.handle(command);
            var subscriptionResource = SubscriptionResourceFromEntityAssembler.toResource(subscription);
            
            LOG.info("Subscription plan changed successfully for tenant: {}", tenantId);
            return ResponseEntity.ok(subscriptionResource);
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to change subscription plan for tenant {}: {}", tenantId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /* ---------- HELPERS ---------- */
    
    private Long requireTenant() {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context missing");
        }
        return tenantId;
    }
} 