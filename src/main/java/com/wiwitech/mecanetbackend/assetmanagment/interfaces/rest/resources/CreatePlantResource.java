// CreatePlantResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Create plant resource
 * This resource is used to create new plants
 */
public record CreatePlantResource(
    String name,
    String address,
    String city,
    String country,
    String contactPhone,
    String contactEmail
) {}