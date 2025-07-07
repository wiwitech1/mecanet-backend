package com.wiwitech.mecanetbackend.workorders.application.internal.queryservices;

import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.queries.*;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.services.WorkOrderQueryService;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WorkOrderQueryServiceImpl implements WorkOrderQueryService {

    private final WorkOrderRepository repository;

    public WorkOrderQueryServiceImpl(WorkOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<WorkOrder> handle(GetWorkOrderByIdQuery query) {
        return repository.findByWorkOrderIdValueAndTenantIdValue(query.workOrderId().getValue(), TenantContext.getCurrentTenantId());
    }

    @Override
    public List<WorkOrder> handle(GetWorkOrdersByStatusQuery query) {
        WorkOrderStatus status = query.status();
        return repository.findByStatusAndTenantIdValue(status, TenantContext.getCurrentTenantId());
    }

    @Override
    public List<WorkOrder> handle(GetWorkOrdersByMachineQuery query) {
        return repository.findByMachineIdValueAndTenantIdValue(query.machineId().getValue(), TenantContext.getCurrentTenantId());
    }

    @Override
    public List<WorkOrder> handle(GetWorkOrdersByTechnicianQuery query) {
        return repository.findByTechnicianAndTenantId(query.technicianId(), TenantContext.getCurrentTenantId());
    }
} 