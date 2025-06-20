package com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.entities;

import com.wiwitech.mecanetbackend.competency.domain.model.valueobjects.SkillId;
import com.wiwitech.mecanetbackend.maintenanceplanning.domain.model.valueobjects.MachineId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "static_tasks")
@NoArgsConstructor
public class StaticTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MachineId machineId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @ElementCollection
    @CollectionTable(name = "static_task_skills", joinColumns = @JoinColumn(name = "task_id"))
    private Set<SkillId> requiredSkills = new HashSet<>();

    public StaticTask(MachineId machineId,
                      String name,
                      String description,
                      Set<SkillId> skills) {
        this.machineId      = machineId;
        this.name           = name;
        this.description    = description;
        this.requiredSkills = skills != null ? skills : new HashSet<>();
    }
}