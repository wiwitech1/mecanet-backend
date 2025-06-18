package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

public record PlantCreatedEvent(
    Long plantId,
    String plantName,
    Long tenantId,
    LocalDateTime occurredOn
) {
    public PlantCreatedEvent(Long plantId, String plantName, Long tenantId) {
        this(plantId, plantName, tenantId, LocalDateTime.now());
    }
}