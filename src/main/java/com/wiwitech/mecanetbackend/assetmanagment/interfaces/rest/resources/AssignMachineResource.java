// AssignMachineResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Assign machine resource
 * This resource is used to assign machines to production lines
 */
public record AssignMachineResource(
    Long productionLineId
) {}