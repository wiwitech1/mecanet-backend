package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.commandservices;

import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.aggregates.MachineMetrics;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.model.commands.RecordMetricCommand;
import com.wiwitech.mecanetbackend.conditionmonitoring.domain.services.MachineMetricsCommandService;
import com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.persistence.jpa.repositories.MachineMetricsRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.outboundservices.MachineCatalogAcl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class MachineMetricsCommandServiceImpl implements MachineMetricsCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(MachineMetricsCommandServiceImpl.class);

    private final MachineMetricsRepository repository;
    private final MachineCatalogAcl        catalogAcl;

    public MachineMetricsCommandServiceImpl(MachineMetricsRepository repository,
                                            MachineCatalogAcl catalogAcl) {
        this.repository = repository;
        this.catalogAcl = catalogAcl;
    }

    @Override
    @Transactional
    public MachineMetrics handle(RecordMetricCommand cmd) {

        Long tenant = TenantContext.getCurrentTenantId();
        if (tenant == null)
            throw new IllegalStateException("Tenant context missing");

        // ---------- ACL check with AssetManagement ----------
        if (!catalogAcl.machineExists(cmd.machineId(), tenant)) {
            throw new IllegalArgumentException("Machine ID not found in AssetManagement");
        }
        // -----------------------------------------------------

        // Obtener o crear agregado
        MachineMetrics metrics = repository
                .findByMachineIdAndTenantIdValue(cmd.machineId(), tenant)
                .orElseGet(() -> new MachineMetrics(cmd.machineId(), new TenantId(tenant)));

        Instant ts = cmd.measuredAt() != null ? cmd.measuredAt() : Instant.now();
        metrics.record(cmd.metricId(), cmd.value(), ts);

        LOG.debug("Metric recorded: machine={}, metric={}, value={}, ts={}",
                cmd.machineId(), cmd.metricId(), cmd.value(), ts);

        return repository.save(metrics);
    }
}