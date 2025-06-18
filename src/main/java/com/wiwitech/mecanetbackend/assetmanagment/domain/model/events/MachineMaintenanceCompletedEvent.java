package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

/**
 * Domain event fired when machine maintenance is completed
 */
public record MachineMaintenanceCompletedEvent(
    Long machineId,
    LocalDateTime occurredOn
) {
    public MachineMaintenanceCompletedEvent(Long machineId) {
        this(machineId, LocalDateTime.now());
    }
}