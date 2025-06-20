package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities;

import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.StaticMaintenancePlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "static_plan_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "day_index"}))
@NoArgsConstructor
public class StaticPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private StaticMaintenancePlan plan;

    @Column(name = "day_index", nullable = false)
    private int dayIndex;        // 1..durationInDays

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_id")
    private Set<StaticTask> tasks = new HashSet<>();

    public StaticPlanItem(StaticMaintenancePlan plan, int dayIndex) {
        this.plan     = plan;
        this.dayIndex = dayIndex;
    }

    public StaticTask addTask(StaticTask task) {
        tasks.add(task);
        return task;
    }
}