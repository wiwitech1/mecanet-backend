package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates;

import com.wiwitech.mecanetbackend.competency.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.DynamicTask;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.PlanType;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.DynamicTaskTriggeredEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.MaintenancePlanCreatedEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@DiscriminatorValue("DYNAMIC")
@Table(name = "dynamic_maintenance_plans")
@NoArgsConstructor
public class DynamicMaintenancePlan extends MaintenancePlan {

    @Embedded
    private MetricDefinitionId metricId;

    @Embedded
    private Threshold threshold;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<DynamicTask> tasks = new HashSet<>();

    private DynamicMaintenancePlan(String name,
                                   PlanPeriod period,
                                   MetricDefinitionId metricId,
                                   Threshold threshold,
                                   TenantId tenantId) {
        super(name, period, PlanType.DYNAMIC, tenantId);
        this.metricId  = metricId;
        this.threshold = threshold;
        addDomainEvent(new MaintenancePlanCreatedEvent(getId(), tenantId.getValue(), PlanType.DYNAMIC));
    }

    public static DynamicMaintenancePlan create(String name,
                                                PlanPeriod period,
                                                MetricDefinitionId metricId,
                                                Threshold threshold,
                                                TenantId tenantId) {
        return new DynamicMaintenancePlan(name, period, metricId, threshold, tenantId);
    }

    public DynamicTask addTask(MachineId machineId,
                               String taskName,
                               String description,
                               Set<SkillId> skills) {
        DynamicTask task = DynamicTask.create(this, machineId, taskName, description, skills);
        tasks.add(task);
        return task;
    }

    /* ---------- disparo por métrica ---------- */
    public void evaluateTrigger(MachineId machineId, double reading) {
        if (!isActiveOn(LocalDate.now())) return;
        if (reading < threshold.getValue())   return;

        tasks.stream()
             .filter(t -> t.canTrigger(machineId))
             .forEach(t -> {
                 t.trigger();
                 addDomainEvent(new DynamicTaskTriggeredEvent(getId(),
                                                              t.getId(),
                                                              machineId.getValue(),
                                                              reading));
             });
    }
}