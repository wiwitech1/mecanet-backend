// PlantResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Plant resource for responses
 */
public record PlantResource(
    Long id,
    String name,
    String address,
    String city,
    String country,
    String contactPhone,
    String contactEmail,
    Boolean active
) {}