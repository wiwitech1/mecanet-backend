package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    boolean existsByIamUserIdValueAndTenantIdValue(Long iamUserId, Long tenantId);
}