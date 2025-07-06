// StopProductionResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Stop production resource
 * This resource is used to stop production with a reason
 */
public record StopProductionResource(
    String reason
) {}