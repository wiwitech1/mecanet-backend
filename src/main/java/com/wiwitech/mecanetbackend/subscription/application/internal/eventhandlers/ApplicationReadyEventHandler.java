package com.wiwitech.mecanetbackend.subscription.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.subscription.domain.model.commands.SeedPlansCommand;
import com.wiwitech.mecanetbackend.subscription.domain.services.PlanCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * ApplicationReadyEventHandler class
 * This class is used to handle the ApplicationReadyEvent for subscription plans seeding
 */
@Service("subscriptionApplicationReadyEventHandler")
public class ApplicationReadyEventHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);
    private final PlanCommandService planCommandService;

    public ApplicationReadyEventHandler(PlanCommandService planCommandService) {
        this.planCommandService = planCommandService;
    }

    /**
     * Handle the ApplicationReadyEvent
     * This method is used to seed the subscription plans
     * @param event the ApplicationReadyEvent to handle
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        LOGGER.info("Starting to verify if subscription plans seeding is needed for {} at {}", 
                   applicationName, currentTimestamp());
        
        try {
            var seedPlansCommand = new SeedPlansCommand();
            planCommandService.handle(seedPlansCommand);
            LOGGER.info("Subscription plans seeding verification finished successfully for {} at {}", 
                       applicationName, currentTimestamp());
        } catch (Exception e) {
            LOGGER.error("Error during subscription plans seeding for {} at {}: {}", 
                        applicationName, currentTimestamp(), e.getMessage(), e);
        }
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
} 