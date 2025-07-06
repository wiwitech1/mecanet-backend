package com.wiwitech.mecanetbackend.subscription.interfaces.rest.resources;

/**
 * Plan attribute resource for API responses
 */
public record PlanAttributeResource(
        Long id,
        String attributeName,
        Integer attributeValue,
        Boolean isUnlimited
) {} 