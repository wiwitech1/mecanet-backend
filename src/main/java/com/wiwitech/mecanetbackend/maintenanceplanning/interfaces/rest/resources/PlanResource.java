package com.wiwitech.mecanetbackend.maintenanceplanning.interfaces.rest.resources;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanStatus;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanType;

import java.time.LocalDate;

public record PlanResource(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        PlanStatus status,
        PlanType planType
) {}