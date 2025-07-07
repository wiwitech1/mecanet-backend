package com.wiwitech.mecanetbackend.workorders.infrastructure.persistence.jpa.repositories;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.TechnicianId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    Optional<WorkOrder> findByWorkOrderIdValueAndTenantIdValue(Long workOrderId, Long tenantId);
    List<WorkOrder> findByStatusAndTenantIdValue(WorkOrderStatus status, Long tenantId);
    boolean existsByWorkOrderIdValueAndTenantIdValue(Long workOrderId, Long tenantId);
    long countByStatusAndTenantIdValue(WorkOrderStatus status, Long tenantId);
    
    List<WorkOrder> findByMachineIdValueAndTenantIdValue(Long machineId, Long tenantId);
    
    @Query("SELECT DISTINCT wo FROM WorkOrder wo JOIN wo.technicians wt WHERE wt.technicianId = :technicianId AND wo.tenantId.value = :tenantId")
    List<WorkOrder> findByTechnicianAndTenantId(@Param("technicianId") TechnicianId technicianId, @Param("tenantId") Long tenantId);
} 