// MachineResource.java
package com.wiwitech.mecanetbackend.assetmanagment.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Machine resource for responses
 */
public record MachineResource(
    Long id,
    String serialNumber,
    String name,
    String manufacturer,
    String model,
    String type,
    Double powerConsumption,
    String status,
    Long productionLineId,
    LocalDateTime lastMaintenanceDate,
    LocalDateTime nextMaintenanceDate
) {}