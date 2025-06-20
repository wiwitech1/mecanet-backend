package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.TaskStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record DynamicTaskResource(
        Long id,
        Long machineId,
        String name,
        String description,
        Set<Long> requiredSkillIds,
        TaskStatus status,
        LocalDateTime triggeredAt
) {}