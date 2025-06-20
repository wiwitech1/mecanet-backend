package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import java.util.Set;

public record StaticTaskResource(
        Long id,
        Long machineId,
        String name,
        String description,
        Set<Long> requiredSkillIds
) {}