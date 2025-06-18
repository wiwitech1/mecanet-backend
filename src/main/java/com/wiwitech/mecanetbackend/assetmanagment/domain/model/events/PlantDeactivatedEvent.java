package com.wiwitech.mecanetbackend.assetmanagment.domain.model.events;

import java.time.LocalDateTime;

public record PlantDeactivatedEvent(
    Long plantId,
    String plantName,
    LocalDateTime occurredOn
) {
    public PlantDeactivatedEvent(Long plantId, String plantName) {
        this(plantId, plantName, LocalDateTime.now());
    }
}