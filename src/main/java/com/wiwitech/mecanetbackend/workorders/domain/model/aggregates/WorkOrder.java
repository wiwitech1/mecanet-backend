package com.wiwitech.mecanetbackend.workorders.domain.model.aggregates;

import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.workorders.domain.model.entities.*;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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

    // Constructor principal (usado por listener de creación)
    public WorkOrder(WorkOrderId workOrderId,
                     DynamicPlanId planId,
                     DynamicTaskId taskId,
                     MachineId machineId,
                     String title,
                     String description,
                     TenantId tenantId) {
        this.workOrderId = workOrderId;
        this.planId = planId;
        this.taskId = taskId;
        this.machineId = machineId;
        this.title = title;
        this.description = description;
        this.status = WorkOrderStatus.NEW;
        this.tenantId = tenantId;
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

    // Otros métodos se implementarán gradualmente
} 