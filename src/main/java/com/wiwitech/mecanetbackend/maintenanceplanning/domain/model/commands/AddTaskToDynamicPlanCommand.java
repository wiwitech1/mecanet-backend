package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands;

import java.util.Set;

public record AddTaskToDynamicPlanCommand(
        Long        planId,
        Long        machineId,
        String      taskName,
        String      description,
        Set<Long>   skillIds
) {}