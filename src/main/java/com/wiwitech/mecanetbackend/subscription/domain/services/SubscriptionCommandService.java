package com.wiwitech.mecanetbackend.subscription.domain.services;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Subscription;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.ChangeSubscriptionPlanCommand;
import com.wiwitech.mecanetbackend.subscription.domain.model.commands.CreateSubscriptionCommand;

public interface SubscriptionCommandService {
    Subscription handle(CreateSubscriptionCommand command);
    Subscription handle(ChangeSubscriptionPlanCommand command);
} 