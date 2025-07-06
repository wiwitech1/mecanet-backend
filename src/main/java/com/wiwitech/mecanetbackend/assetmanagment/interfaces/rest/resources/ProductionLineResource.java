// ProductionLineResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Production line resource for responses
 */
public record ProductionLineResource(
    Long id,
    String name,
    String code,
    Integer maxUnitsPerHour,
    String unit,
    String status,
    Long plantId
) {}