package com.wiwitech.mecanetbackend.maintenanceplanning.domain.services;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.DynamicTask;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticPlanItem;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticTask;

public interface MaintenancePlanCommandService {
    DynamicMaintenancePlan handle(CreateDynamicPlanCommand cmd);
    DynamicTask            handle(AddTaskToDynamicPlanCommand cmd);

    StaticMaintenancePlan  handle(CreateStaticPlanCommand cmd);
    StaticPlanItem         handle(AddItemToStaticPlanCommand cmd);
    StaticTask             handle(AddTaskToStaticItemCommand cmd);

    void                   handle(DeactivatePlanCommand cmd);
}