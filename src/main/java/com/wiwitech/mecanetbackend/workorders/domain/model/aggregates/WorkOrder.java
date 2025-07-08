package com.wiwitech.mecanetbackend.workorders.domain.model.aggregates;

import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.workorders.domain.model.entities.*;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.workorders.domain.exceptions.InvalidWorkOrderStateException;
import com.wiwitech.mecanetbackend.workorders.domain.exceptions.TechnicianLimitExceededException;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.UpdateMaterialsCommand;
import com.wiwitech.mecanetbackend.workorders.domain.model.events.WorkOrderStartedEvent;
import com.wiwitech.mecanetbackend.workorders.domain.model.events.WorkOrderCompletedEvent;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.InventoryItemId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "work_orders")
@NoArgsConstructor
public class WorkOrder extends AuditableAbstractAggregateRoot<WorkOrder> {

    @Embedded
    private WorkOrderId workOrderId;

    @Embedded
    private DynamicPlanId planId;

    @Embedded
    private DynamicTaskId taskId;

    @Embedded
    private MachineId machineId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    @Embedded
    private Schedule schedule;        // puede ser null al inicio

    @Column(name = "max_technicians")
    private Integer maxTechnicians;

    // collections -----------------------------------------------------
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "work_order_id")
    private Set<WorkOrderTechnician> technicians = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "work_order_id")
    private Set<WorkOrderMaterial> materials = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "work_order_id")
    private List<WorkOrderComment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "work_order_id")
    private List<WorkOrderPhoto> photos = new ArrayList<>();

    // ejecución real
    @Embedded
    private ExecutionWindow executionWindow;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "tenant_id", nullable = false))
    private TenantId tenantId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "work_order_required_skills", joinColumns = @JoinColumn(name = "work_order_id"))
    private Set<SkillId> requiredSkillIds = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String conclusions;

    // Constructor principal (usado por listener de creación)
    public WorkOrder(WorkOrderId workOrderId,
                     DynamicPlanId planId,
                     DynamicTaskId taskId,
                     MachineId machineId,
                     String title,
                     String description,
                     Set<SkillId> requiredSkillIds,
                     TenantId tenantId) {
        this.workOrderId = workOrderId;
        this.planId = planId;
        this.taskId = taskId;
        this.machineId = machineId;
        this.title = title;
        this.description = description;
        this.status = WorkOrderStatus.NEW;
        this.tenantId = tenantId;
        this.requiredSkillIds = requiredSkillIds != null ? requiredSkillIds : new HashSet<>();
    }

    // Métodos de dominio mínimos (place-holders)
    public void defineSchedule(Schedule schedule, int maxTechnicians) {
        if (status != WorkOrderStatus.NEW)
            throw new IllegalStateException("Schedule can only be defined in NEW state");
        this.schedule = schedule;
        this.maxTechnicians = maxTechnicians;
    }

    public void publish() {
        if (status != WorkOrderStatus.NEW)
            throw new IllegalStateException("Cannot publish from state " + status);
        if (schedule == null || maxTechnicians == null)
            throw new IllegalStateException("Schedule and maxTechnicians must be defined before publish");
        this.status = WorkOrderStatus.PUBLISHED;
    }

    public void joinTechnician(TechnicianId technicianId, TechnicianRole role) {
        if (status != WorkOrderStatus.PUBLISHED)
            throw new InvalidWorkOrderStateException("Technicians can only join a PUBLISHED work order");
        if (technicians.size() >= maxTechnicians)
            throw new TechnicianLimitExceededException("Work order already at max capacity");
        if (technicians.stream().anyMatch(t -> t.getTechnicianId().equals(technicianId)))
            throw new IllegalStateException("Technician already joined");
        technicians.add(new WorkOrderTechnician(technicianId, role));
    }

    public void leaveTechnician(TechnicianId technicianId, String reason) {
        if (status != WorkOrderStatus.PUBLISHED)
            throw new InvalidWorkOrderStateException("Technicians can only leave while work order is PUBLISHED");
        technicians.stream()
                .filter(t -> t.getTechnicianId().equals(technicianId))
                .findFirst()
                .ifPresentOrElse(t -> t.withdraw(reason),
                    () -> {throw new IllegalStateException("Technician not part of work order");});
    }

    public void updateMaterials(Set<UpdateMaterialsCommand.MaterialLine> lines) {
        if (status != WorkOrderStatus.PUBLISHED)
            throw new InvalidWorkOrderStateException("Materials can only be edited while PUBLISHED");
        // reemplazar o actualizar
        lines.forEach(line -> {
            WorkOrderMaterial existing = materials.stream()
                    .filter(m -> m.getItemId().getValue().equals(line.itemId()))
                    .findFirst()
                    .orElse(null);
            if (existing == null) {
                materials.add(new WorkOrderMaterial(new InventoryItemId(line.itemId()), line.sku(), line.name(), line.quantity()));
            } else {
                existing.updateRequestedQty(line.quantity());
            }
        });
    }

    public void moveToReview() {
        if (status != WorkOrderStatus.PUBLISHED)
            throw new InvalidWorkOrderStateException("Can only move to REVIEW from PUBLISHED");
        this.status = WorkOrderStatus.REVIEW;
    }

    public void setPendingExecution() {
        if (status != WorkOrderStatus.REVIEW)
            throw new InvalidWorkOrderStateException("Can only set PENDING_EXECUTION from REVIEW");
        this.status = WorkOrderStatus.PENDING_EXECUTION;
    }

    public void start(LocalDateTime startAt) {
        if (status != WorkOrderStatus.PENDING_EXECUTION)
            throw new InvalidWorkOrderStateException("Can only start from PENDING_EXECUTION");
        this.executionWindow = new ExecutionWindow(startAt, null);
        this.status = WorkOrderStatus.IN_EXECUTION;
        addDomainEvent(new WorkOrderStartedEvent(workOrderId, startAt, tenantId.getValue()));
    }

    public void complete(LocalDateTime endAt, String conclusions) {
        if (status != WorkOrderStatus.IN_EXECUTION)
            throw new InvalidWorkOrderStateException("Can only complete from IN_EXECUTION");
        this.executionWindow.end(endAt);
        this.conclusions = conclusions;
        this.status = WorkOrderStatus.COMPLETED;
        addDomainEvent(new WorkOrderCompletedEvent(workOrderId, endAt, tenantId.getValue()));
    }

    public void addComment(Long authorUserId, String text) {
        if (status != WorkOrderStatus.IN_EXECUTION)
            throw new InvalidWorkOrderStateException("Comments allowed only while IN_EXECUTION");
        comments.add(new WorkOrderComment(authorUserId, text));
    }

    public void addPhoto(Long authorUserId, String url) {
        if (status != WorkOrderStatus.IN_EXECUTION)
            throw new InvalidWorkOrderStateException("Photos allowed only while IN_EXECUTION");
        photos.add(new WorkOrderPhoto(url, authorUserId));
    }

    public void updateFinalQuantities(java.util.Map<Long, Integer> finalQuantities) {
        if (status != WorkOrderStatus.IN_EXECUTION)
            throw new InvalidWorkOrderStateException("Final quantities can only be updated while IN_EXECUTION");
        
        finalQuantities.forEach((itemId, finalQty) -> {
            WorkOrderMaterial material = materials.stream()
                    .filter(m -> m.getItemId().getValue().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Material not found: " + itemId));
            
            material.setFinalQty(finalQty);
        });
    }

    @Override
    public String toString() {
        return "WorkOrder{" + workOrderId + ", status=" + status + '}';
    }
} 