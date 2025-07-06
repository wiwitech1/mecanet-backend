// RegisterMachineResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

/**
 * Register machine resource
 * This resource is used to register new machines
 */
public record RegisterMachineResource(
    String serialNumber,
    String name,
    String manufacturer,
    String model,
    String type,
    Double powerConsumption
) {}