package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities.StaticPlanItem;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.MaintenancePlanCreatedEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.events.StaticPlanDayGeneratedEvent;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.*;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@DiscriminatorValue("STATIC")
@Table(name = "static_maintenance_plans")
@NoArgsConstructor
public class StaticMaintenancePlan extends MaintenancePlan {

    @Embedded
    private ProductionLineId lineId;

    @Column(name = "cycle_period_days", nullable = false)
    private int cyclePeriodInDays;

    @Column(name = "duration_days", nullable = false)
    private int durationInDays;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<StaticPlanItem> items = new HashSet<>();

    private StaticMaintenancePlan(String name,
                                  PlanPeriod period,
                                  ProductionLineId lineId,
                                  int cyclePeriodInDays,
                                  int durationInDays,
                                  TenantId tenantId) {
        super(name, period, PlanType.STATIC, tenantId);
        if (cyclePeriodInDays <= 0 || durationInDays <= 0 || durationInDays > cyclePeriodInDays)
            throw new IllegalArgumentException("Parámetros de ciclo/duración inválidos");
        this.lineId            = lineId;
        this.cyclePeriodInDays = cyclePeriodInDays;
        this.durationInDays    = durationInDays;
        addDomainEvent(new MaintenancePlanCreatedEvent(getId(), tenantId.getValue(), PlanType.STATIC));
    }

    public static StaticMaintenancePlan create(String name,
                                               PlanPeriod period,
                                               ProductionLineId lineId,
                                               int cyclePeriodInDays,
                                               int durationInDays,
                                               TenantId tenantId) {
        return new StaticMaintenancePlan(name, period, lineId, cyclePeriodInDays, durationInDays, tenantId);
    }

    /* ---------- ítems ---------- */
    public StaticPlanItem addItem(int dayIndex) {
        if (dayIndex < 1 || dayIndex > durationInDays)
            throw new IllegalArgumentException("dayIndex fuera de rango");
        StaticPlanItem item = new StaticPlanItem(this, dayIndex);
        items.add(item);
        return item;
    }

    /* ---------- generación diaria ---------- */
    public void generateWorkOrdersFor(LocalDate currentDate) {
        if (!isActiveOn(currentDate)) return;

        long daysSinceStart = ChronoUnit.DAYS.between(period.getStartDate(), currentDate);
        if (daysSinceStart < 0) return;

        int dayOfCycle = (int) (daysSinceStart % cyclePeriodInDays) + 1;  // 1-based
        if (dayOfCycle > durationInDays) return; // fuera de la ventana del plan

        int finalDayOfCycle = dayOfCycle;
        items.stream()
             .filter(i -> i.getDayIndex() == finalDayOfCycle)
             .findFirst()
             .ifPresent(i -> addDomainEvent(new StaticPlanDayGeneratedEvent(getId(),
                                                                            currentDate.minusDays(finalDayOfCycle - 1),
                                                                            finalDayOfCycle)));
    }
}