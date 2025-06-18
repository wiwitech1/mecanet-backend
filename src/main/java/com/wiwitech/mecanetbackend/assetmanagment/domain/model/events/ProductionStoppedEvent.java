package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

public record ProductionStoppedEvent(
    Long productionLineId,
    String productionLineName,
    Long plantId,
    String reason,
    LocalDateTime occurredOn
) {
    public ProductionStoppedEvent(Long productionLineId, String productionLineName, Long plantId, String reason) {
        this(productionLineId, productionLineName, plantId, reason, LocalDateTime.now());
    }
}