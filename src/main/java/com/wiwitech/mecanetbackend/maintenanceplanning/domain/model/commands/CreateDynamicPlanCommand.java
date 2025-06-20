package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands;

import java.time.LocalDate;

public record CreateDynamicPlanCommand(
        String      name,
        LocalDate   startDate,
        LocalDate   endDate,
        Long        metricDefinitionId,
        Double      threshold
) {}