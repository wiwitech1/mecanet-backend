package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.Technician;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.SkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    boolean existsByIamUserIdValueAndTenantIdValue(Long iamUserId, Long tenantId);
    Optional<Technician> findByIamUserIdValueAndTenantIdValue(Long iamUserId, Long tenantId);
    
    List<Technician> findByCurrentStatusAndTenantIdValue(TechnicianStatus status, Long tenantId);
    
    @Query("SELECT t FROM Technician t JOIN t.skills s WHERE s = :skillId AND t.tenantId.value = :tenantId")
    List<Technician> findBySkillAndTenantId(@Param("skillId") SkillId skillId, @Param("tenantId") Long tenantId);
}