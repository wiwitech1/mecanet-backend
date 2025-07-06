package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands;

import java.time.LocalDate;

public record CreateStaticPlanCommand(
        String    name,
        LocalDate startDate,
        LocalDate endDate,
        Long      productionLineId,
        int       cyclePeriodInDays,
        int       durationInDays
) {}