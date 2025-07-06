package com.wiwitech.mecanetbackend.subscription.domain.services;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Plan;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.CreatePlanCommand;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.DeactivatePlanCommand;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.SeedPlansCommand;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.UpdatePlanCommand;

public interface PlanCommandService {
    Plan handle(CreatePlanCommand command);
    Plan handle(UpdatePlanCommand command);
    Plan handle(DeactivatePlanCommand command);
    void handle(SeedPlansCommand command);
} 