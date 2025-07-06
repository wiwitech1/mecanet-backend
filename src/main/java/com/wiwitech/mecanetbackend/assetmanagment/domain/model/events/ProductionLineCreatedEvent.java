package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

public record ProductionLineCreatedEvent(
    Long productionLineId,
    String name,
    Long plantId,
    Long tenantId,
    LocalDateTime occurredOn
) {
    public ProductionLineCreatedEvent(Long productionLineId, String name, Long plantId, Long tenantId) {
        this(productionLineId, name, plantId, tenantId, LocalDateTime.now());
    }
}