package com.wiwitech.mecanetbackend.maintenanceplanning.application.internal.commandservices;

import com.wiwitech.mecanetbackend.competency.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.commands.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.services.MaintenancePlanCommandService;
import com.wiwitech.mecanetbackend.maintenanceplanning.infrastructure.persistence.jpa.repositories.MaintenancePlanRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaintenancePlanCommandServiceImpl implements MaintenancePlanCommandService {

    private final MaintenancePlanRepository repository;

    public MaintenancePlanCommandServiceImpl(MaintenancePlanRepository repository) {
        this.repository = repository;
    }

    /* ---------- PLAN DINÁMICO ---------- */
    @Override
    public DynamicMaintenancePlan handle(CreateDynamicPlanCommand cmd) {
        DynamicMaintenancePlan plan = DynamicMaintenancePlan.create(
                cmd.name(),
                new PlanPeriod(cmd.startDate(), cmd.endDate()),
                new MetricDefinitionId(cmd.metricDefinitionId()),
                new Threshold(cmd.threshold()),
                new TenantId(requireTenant()));
        return (DynamicMaintenancePlan) repository.save(plan);
    }

    @Override
    public DynamicTask handle(AddTaskToDynamicPlanCommand cmd) {
        DynamicMaintenancePlan plan = (DynamicMaintenancePlan) loadPlanOrThrow(cmd.planId());

        Set<SkillId> skills = toSkillIds(cmd.skillIds());
        MachineId machineId = new MachineId(cmd.machineId());
        String taskName = cmd.taskName();
        String description = cmd.description();
        
        plan.addTask(machineId, taskName, description, skills);
        repository.saveAndFlush(plan); // ✅ FLUSH INMEDIATO - persiste en BD

        // ✅ RECARGAMOS el plan desde BD con todas las tasks persistidas
        DynamicMaintenancePlan reloadedPlan = (DynamicMaintenancePlan) loadPlanOrThrow(cmd.planId());
        
        // ✅ ENCONTRAMOS la task recién creada
        return reloadedPlan.getTasks().stream()
                .filter(t -> t.getMachineId().equals(machineId) 
                          && t.getName().equals(taskName)
                          && t.getDescription().equals(description))
                .max((t1, t2) -> Long.compare(t1.getId(), t2.getId())) // La más reciente
                .orElseThrow(() -> new IllegalStateException("Task no encontrada después de guardar"));
    }

    /* ---------- PLAN ESTÁTICO ---------- */
    @Override
    public StaticMaintenancePlan handle(CreateStaticPlanCommand cmd) {
        StaticMaintenancePlan plan = StaticMaintenancePlan.create(
                cmd.name(),
                new PlanPeriod(cmd.startDate(), cmd.endDate()),
                new ProductionLineId(cmd.productionLineId()),
                cmd.cyclePeriodInDays(),
                cmd.durationInDays(),
                new TenantId(requireTenant()));
        return (StaticMaintenancePlan) repository.save(plan);
    }

    @Override
    public StaticPlanItem handle(AddItemToStaticPlanCommand cmd) {
        StaticMaintenancePlan plan = (StaticMaintenancePlan) loadPlanOrThrow(cmd.planId());
        int dayIndex = cmd.dayIndex();
        
        plan.addItem(dayIndex);
        repository.saveAndFlush(plan); // ✅ FLUSH INMEDIATO - persiste en BD

        // ✅ RECARGAMOS el plan desde BD con todos los items persistidos
        StaticMaintenancePlan reloadedPlan = (StaticMaintenancePlan) loadPlanOrThrow(cmd.planId());
        
        // ✅ ENCONTRAMOS el item recién creado por dayIndex
        return reloadedPlan.getItems().stream()
                .filter(item -> item.getDayIndex() == dayIndex)
                .max((i1, i2) -> Long.compare(i1.getId(), i2.getId())) // El más reciente
                .orElseThrow(() -> new IllegalStateException("Item no encontrado después de guardar"));
    }

    @Override
    public StaticTask handle(AddTaskToStaticItemCommand cmd) {
        StaticMaintenancePlan plan = (StaticMaintenancePlan) loadPlanOrThrow(cmd.planId());

        StaticPlanItem item = plan.getItems()
                                  .stream()
                                  .filter(i -> i.getId().equals(cmd.itemId()))
                                  .findFirst()
                                  .orElseThrow(() -> new IllegalArgumentException("Ítem no encontrado"));

        MachineId machineId = new MachineId(cmd.machineId());
        String taskName = cmd.taskName();
        String description = cmd.description();
        Set<SkillId> skills = toSkillIds(cmd.skillIds());
        
        StaticTask task = new StaticTask(machineId, taskName, description, skills);
        item.addTask(task);
        repository.saveAndFlush(plan); // ✅ FLUSH INMEDIATO - persiste en BD

        // ✅ RECARGAMOS el plan desde BD
        StaticMaintenancePlan reloadedPlan = (StaticMaintenancePlan) loadPlanOrThrow(cmd.planId());
        
        // ✅ ENCONTRAMOS el item y luego la task recién creada
        StaticPlanItem reloadedItem = reloadedPlan.getItems().stream()
                .filter(i -> i.getId().equals(cmd.itemId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item no encontrado"));
                
        return reloadedItem.getTasks().stream()
                .filter(t -> t.getMachineId().equals(machineId) 
                          && t.getName().equals(taskName)
                          && t.getDescription().equals(description))
                .max((t1, t2) -> Long.compare(t1.getId(), t2.getId())) // La más reciente
                .orElseThrow(() -> new IllegalStateException("Task no encontrada después de guardar"));
    }

    /* ---------- DESACTIVAR ---------- */
    @Override
    public void handle(DeactivatePlanCommand cmd) {
        repository.findByIdAndTenantIdValue(cmd.planId(), requireTenant())
                  .ifPresent(p -> {
                      p.deactivate();
                      repository.save(p);
                  });
    }

    /* ---------- helpers ---------- */
    private MaintenancePlan loadPlanOrThrow(Long planId) {
        return repository.findByIdAndTenantIdValue(planId, requireTenant())
                         .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
    }

    private Long requireTenant() {
        Long t = TenantContext.getCurrentTenantId();
        if (t == null) throw new IllegalStateException("Tenant context missing");
        return t;
    }

    private Set<SkillId> toSkillIds(Set<Long> ids) {
        return ids == null ? Collections.emptySet()
                           : ids.stream().map(SkillId::new).collect(Collectors.toSet());
    }
}