package com.wiwitech.mecanetbackend.maintenanceplanning.domain.services;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.MaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface MaintenancePlanQueryService {
    Optional<MaintenancePlan>          handle(GetMaintenancePlanByIdQuery query);
    List<DynamicMaintenancePlan>       handle(GetDynamicPlansByMetricAndMachineQuery query);
    List<StaticMaintenancePlan>         handle(GetStaticPlansScheduledForDateQuery query);
    List<DynamicMaintenancePlan> handle(GetAllDynamicPlansQuery query);
    List<StaticMaintenancePlan>  handle(GetAllStaticPlansQuery   query);
}