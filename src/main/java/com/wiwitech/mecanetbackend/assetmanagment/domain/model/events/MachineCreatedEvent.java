package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

/** Domain event fired when a new machine is registered. */
public record MachineCreatedEvent(
        Long machineId,
        Long tenantId
) {}