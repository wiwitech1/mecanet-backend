package com.wiwitech.mecanetbackend.workorders.domain.model.aggregates;

import com.wiwitech.mecanetbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.EmailAddress;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.PhoneNumber;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.Shift;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import jakarta.persistence.*;
import lombok.Getter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(
    name = "technicians",
    uniqueConstraints = @UniqueConstraint(columnNames = {"iam_user_id"})
)
public class Technician extends AuditableAbstractAggregateRoot<Technician> {

    @Embedded
    private UserId iamUserId;                       // ✔ VO

    @Column(nullable = false, length = 50)
    private String username;

    private String firstName;
    private String lastName;

    @Embedded
    private EmailAddress email;

    @Embedded
    @AttributeOverride(name = "value",
        column = @Column(name = "tenant_id", nullable = false))
    private TenantId tenantId;

    @Embedded
    private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnicianStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Shift shift;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "technician_skills",
            joinColumns = @JoinColumn(name = "technician_id"))
    private Set<SkillId> skills = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Technician supervisor;

    // Constructor sin parámetros para JPA (solo uno)
    protected Technician() {}

    // Constructor con parámetros
    public Technician(UserId iamUserId,
                      String username,
                      String firstName,
                      String lastName,
                      EmailAddress email,
                      TenantId tenantId) {
        this.iamUserId = iamUserId;
        this.username  = username;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.tenantId  = tenantId;
        this.phoneNumber = null;
        this.currentStatus = TechnicianStatus.AVAILABLE;
        this.shift = Shift.DAY;
    }

    /* setters opcionales para permitir actualización futura */
    public void updatePhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void changeStatus(TechnicianStatus status) {
        this.currentStatus = status;
    }
    public void addSkill(SkillId skillId) {
        this.skills.add(skillId);
    }
    public void assignSupervisor(Technician supervisor) {
        this.supervisor = supervisor;
    }
}