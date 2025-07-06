package com.wiwitech.mecanetbackend.subscription.domain.services;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.domain.model.entities.PlanAttribute;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetAllActivePlansQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanAttributesQuery;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetPlanByIdQuery;

import java.util.List;
import java.util.Optional;

public interface PlanQueryService {
    List<Plan> handle(GetAllActivePlansQuery query);
    Optional<Plan> handle(GetPlanByIdQuery query);
    List<PlanAttribute> handle(GetPlanAttributesQuery query);
} 