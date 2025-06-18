package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.valueobjects.ProductionLineStatus;

public record ProductionStartedEvent(
    Long productionLineId,
    String productionLineName,
    Long plantId,
    ProductionLineStatus previousStatus,
    LocalDateTime occurredOn
) {
    public ProductionStartedEvent(Long productionLineId, String productionLineName, Long plantId, ProductionLineStatus previousStatus) {
        this(productionLineId, productionLineName, plantId, previousStatus, LocalDateTime.now());
    }
}