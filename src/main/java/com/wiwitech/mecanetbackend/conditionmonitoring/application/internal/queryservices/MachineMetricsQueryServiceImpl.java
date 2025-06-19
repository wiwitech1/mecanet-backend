package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.queryservices;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MachineMetrics;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.queries.*;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MachineMetricsQueryService;
import com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories.MachineMetricsRepository;
import com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories.MetricReadingRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class MachineMetricsQueryServiceImpl implements MachineMetricsQueryService {

    private final MachineMetricsRepository metricsRepo;
    private final MetricReadingRepository readingRepo;

    public MachineMetricsQueryServiceImpl(MachineMetricsRepository metricsRepo,
                                          MetricReadingRepository readingRepo) {
        this.metricsRepo = metricsRepo;
        this.readingRepo = readingRepo;
    }

    @Override
    public Map<Long, ?> handle(GetCurrentMetricsByMachineQuery q) {
        Long tenant = TenantContext.getCurrentTenantId();
        return metricsRepo.findByMachineIdAndTenantIdValue(q.machineId(), tenant)
                .map(MachineMetrics::getCurrentReadings)
                .orElse(Map.of());
    }

    @Override
    public Optional<?> handle(GetCurrentMetricQuery q) {
        Long tenant = TenantContext.getCurrentTenantId();
        return metricsRepo.findByMachineIdAndTenantIdValue(q.machineId(), tenant)
                .map(mm -> mm.getCurrentReadings().get(q.metricId()));
    }

    @Override
    public Page<?> handle(GetMetricReadingsQuery q) {
        Long tenant = TenantContext.getCurrentTenantId();
        return readingRepo.findByMachineIdAndMetricIdAndMeasuredAtBetween(
                q.machineId(), q.metricId(),
                q.from(), q.to(),
                PageRequest.of(q.page(), q.size()));
    }
}