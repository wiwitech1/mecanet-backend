package com.wiwitech.mecanetbackend.subscription.application.internal.queryservices;

import com.wiwitech.mecanetbackend.subscription.domain.model.aggregates.Subscription;
import com.wiwitech.mecanetbackend.subscription.domain.model.queries.GetSubscriptionByTenantQuery;
import com.wiwitech.mecanetbackend.subscription.domain.services.SubscriptionQueryService;
import com.wiwitech.mecanetbackend.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionQueryServiceImpl.class);
    private final SubscriptionRepository repository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByTenantQuery query) {
        LOG.debug("Getting subscription for tenant: {}", query.tenantId());
        
        Optional<Subscription> subscription = repository.findByTenantIdValue(query.tenantId());
        
        if (subscription.isPresent()) {
            LOG.debug("Subscription found for tenant: {}", query.tenantId());
        } else {
            LOG.debug("Subscription not found for tenant: {}", query.tenantId());
        }
        
        return subscription;
    }
} 