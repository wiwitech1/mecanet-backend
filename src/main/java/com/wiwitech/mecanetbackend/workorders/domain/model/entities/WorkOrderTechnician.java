package com.wiwitech.mecanetbackend.workorders.domain.model.entities;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianRole;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatusInWO;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "work_order_technicians")
@NoArgsConstructor
public class WorkOrderTechnician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TechnicianId technicianId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnicianRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnicianStatusInWO status;

    private String withdrawalReason;

    @Column(nullable = false)
    private LocalDateTime joinedAt;
    private LocalDateTime withdrawnAt;

    public WorkOrderTechnician(TechnicianId technicianId, TechnicianRole role) {
        this.technicianId = technicianId;
        this.role = role;
        this.status = TechnicianStatusInWO.JOINED;
        this.joinedAt = LocalDateTime.now();
    }

    public void withdraw(String reason) {
        this.status = TechnicianStatusInWO.WITHDRAWN;
        this.withdrawalReason = reason;
        this.withdrawnAt = LocalDateTime.now();
    }
} 