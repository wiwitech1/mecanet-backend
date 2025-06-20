package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import java.util.Set;

public record StaticPlanItemResource(
        Long id,
        Integer dayIndex,
        Set<StaticTaskResource> tasks
) {}