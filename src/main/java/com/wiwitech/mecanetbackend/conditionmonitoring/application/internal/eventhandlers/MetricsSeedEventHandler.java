package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.eventhandlers;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands.SeedMetricsCommand;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MetricDefinitionCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Se encarga de sembrar las métricas globales al arrancar la aplicación.
 */
@Component("metricsSeedEventHandler")        // ← nombre único de bean
public class MetricsSeedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsSeedEventHandler.class);
    private final MetricDefinitionCommandService service;

    public MetricsSeedEventHandler(MetricDefinitionCommandService service) {
        this.service = service;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        LOG.info("Seeding default metrics...");
        service.handle(new SeedMetricsCommand());
        LOG.info("Metric seeding finished.");
    }
}