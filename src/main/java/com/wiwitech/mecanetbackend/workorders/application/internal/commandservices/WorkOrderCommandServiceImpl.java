package com.wiwitech.mecanetbackend.workorders.application.internal.commandservices;

import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.workorders.domain.exceptions.WorkOrderNotFoundException;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.*;
import com.wiwitech.mecanetbackend.workorders.domain.services.WorkOrderCommandService;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.WorkOrderRepository;
import com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories.TechnicianRepository;
import com.wiwitech.mecanetbackend.workorders.interfaces.acl.InventoryContextFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianRole;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkOrderCommandServiceImpl implements WorkOrderCommandService {

    private final WorkOrderRepository workOrderRepo;
    private final TechnicianRepository technicianRepo;
    private final InventoryContextFacade inventoryAcl;

    /* ---------- helpers ---------- */
    private WorkOrder loadWorkOrder(WorkOrderId id) {
        Long tenant = TenantContext.getCurrentTenantId();
        return workOrderRepo.findByWorkOrderIdValueAndTenantIdValue(id.getValue(), tenant)
                .orElseThrow(() -> new WorkOrderNotFoundException("WorkOrder not found: " + id));
    }

    /* ---------- COMMANDS ---------- */
    @Override
    @Transactional
    public WorkOrder handle(CreateWorkOrderCommand cmd) {
        log.info("Creating work order {}", cmd.workOrderId());
        WorkOrder wo = new WorkOrder(
                cmd.workOrderId(),
                cmd.planId(),
                cmd.taskId(),
                cmd.machineId(),
                cmd.title(),
                cmd.description(),
                cmd.requiredSkillIds().stream()
                        .map(SkillId::new)
                        .collect(Collectors.toSet()),
                cmd.tenantId());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(DefineScheduleCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.defineSchedule(cmd.schedule(), cmd.maxTechnicians());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(PublishWorkOrderCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.publish();
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(MoveWorkOrderToReviewCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.moveToReview();
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(SetPendingExecutionCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.setPendingExecution();
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(JoinWorkOrderCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        var tech = technicianRepo.findById(cmd.technicianId().getValue())
                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));
        // Skill validation removed - any technician can join any work order
        wo.joinTechnician(cmd.technicianId(),
                tech.getSupervisor() == null ? TechnicianRole.MEMBER : TechnicianRole.SUPERVISOR);
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(LeaveWorkOrderCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.leaveTechnician(cmd.technicianId(), "voluntary");
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(UpdateMaterialsCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        // No material reservations - materials are only deducted when work order completes
        wo.updateMaterials(cmd.materials());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(StartWorkOrderCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        // No reservations to consume - stock is only deducted when work order completes
        wo.start(cmd.startAt());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(CompleteWorkOrderCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        
        // Build final quantities map from work order materials
        var finalQtyPerItem = wo.getMaterials().stream()
                .collect(Collectors.toMap(
                    material -> material.getItemId().getValue(),
                    material -> material.getFinalQty() != null ? material.getFinalQty() : material.getRequestedQty()
                ));
        
        // Deduct stock for consumed materials
        if (!finalQtyPerItem.isEmpty()) {
            inventoryAcl.deductStock(cmd.workOrderId(), finalQtyPerItem);
        }
        
        wo.complete(cmd.endAt(), cmd.conclusions());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(AddCommentCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.addComment(cmd.authorUserId(), cmd.text());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(AddPhotoCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.addPhoto(cmd.authorUserId(), cmd.url());
        return workOrderRepo.save(wo);
    }

    @Override
    @Transactional
    public WorkOrder handle(UpdateFinalQuantitiesCommand cmd) {
        WorkOrder wo = loadWorkOrder(cmd.workOrderId());
        wo.updateFinalQuantities(cmd.finalQuantities());
        return workOrderRepo.save(wo);
    }
} 