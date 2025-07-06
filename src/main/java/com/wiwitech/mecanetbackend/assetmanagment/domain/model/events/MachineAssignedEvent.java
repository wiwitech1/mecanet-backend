package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

/**
 * Domain event fired when a machine is assigned to a production line
 */
public record MachineAssignedEvent(
    Long machineId,
    Long productionLineId,
    LocalDateTime occurredOn
) {
    public MachineAssignedEvent(Long machineId, Long productionLineId) {
        this(machineId, productionLineId, LocalDateTime.now());
    }
}