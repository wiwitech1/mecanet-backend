package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.MachineStatus;

/**
 * Domain event fired when machine maintenance starts
 */
public record MachineMaintenanceStartedEvent(
    Long machineId,
    MachineStatus previousStatus,
    LocalDateTime occurredOn
) {
    public MachineMaintenanceStartedEvent(Long machineId, MachineStatus previousStatus) {
        this(machineId, previousStatus, LocalDateTime.now());
    }
}