package com.wiwitech.mecanetbackend.subscription.domain.services;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Subscription;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetSubscriptionByTenantQuery;

import java.util.Optional;

public interface SubscriptionQueryService {
    Optional<Subscription> handle(GetSubscriptionByTenantQuery query);
} 