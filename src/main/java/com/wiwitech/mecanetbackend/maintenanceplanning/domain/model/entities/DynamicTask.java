package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities;

import com.wiwitech.mecanetbackend.competency.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.aggregates.DynamicMaintenancePlan;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.TaskStatus;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.MachineId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "dynamic_tasks")
@NoArgsConstructor
public class DynamicTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private DynamicMaintenancePlan plan;

    @Embedded
    private MachineId machineId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @ElementCollection
    @CollectionTable(name = "dynamic_task_skills", joinColumns = @JoinColumn(name = "task_id"))
    private Set<SkillId> requiredSkills = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime triggeredAt;

    private DynamicTask(DynamicMaintenancePlan plan,
                        MachineId machineId,
                        String name,
                        String description,
                        Set<SkillId> skills) {
        this.plan          = plan;
        this.machineId     = machineId;
        this.name          = name;
        this.description   = description;
        this.requiredSkills= skills != null ? new HashSet<>(skills) : new HashSet<>();
        this.status        = TaskStatus.PENDING;
    }

    public static DynamicTask create(DynamicMaintenancePlan plan,
                                     MachineId machineId,
                                     String name,
                                     String description,
                                     Set<SkillId> skills) {
        return new DynamicTask(plan, machineId, name, description, skills);
    }

    public boolean canTrigger(MachineId m) {
        return status == TaskStatus.PENDING && machineId.equals(m);
    }

    public void trigger() {
        this.status      = TaskStatus.TRIGGERED;
        this.triggeredAt = LocalDateTime.now();
    }
}