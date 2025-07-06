// UpdatePlantResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Update plant resource
 * This resource is used to update existing plants
 */
public record UpdatePlantResource(
    String name,
    String address,
    String city,
    String country,
    String contactPhone,
    String contactEmail
) {}