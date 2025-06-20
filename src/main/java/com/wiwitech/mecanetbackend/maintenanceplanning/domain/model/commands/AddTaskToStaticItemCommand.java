package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands;

import java.util.Set;

public record AddTaskToStaticItemCommand(
        Long      planId,
        Long      itemId,
        Long      machineId,
        String    taskName,
        String    description,
        Set<Long> skillIds
) {}