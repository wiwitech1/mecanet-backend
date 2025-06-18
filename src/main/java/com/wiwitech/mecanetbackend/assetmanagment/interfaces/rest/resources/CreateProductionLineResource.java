// CreateProductionLineResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Create production line resource
 * This resource is used to create new production lines
 */
public record CreateProductionLineResource(
    String name,
    String code,
    Integer maxUnitsPerHour,
    String unit,
    Long plantId
) {}