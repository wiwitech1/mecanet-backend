package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events;

public record DynamicTaskTriggeredEvent(Long planId, Long taskId, Long machineId, Double reading) {}