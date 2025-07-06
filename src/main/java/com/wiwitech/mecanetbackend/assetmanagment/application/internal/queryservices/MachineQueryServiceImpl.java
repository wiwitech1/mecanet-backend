package com.wiwitech.mecanetbackend.assetmanagment.application.internal.queryservices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Machine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAllMachinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetAvailableMachinesQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachineByIdQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachineBySerialNumberQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachinesByProductionLineQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.queries.GetMachinesDueForMaintenanceQuery;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.MachineQueryService;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.MachineRepository;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Machine query service implementation
 * Handles machine query operations with multitenancy support
 */
@Service
@Slf4j
public class MachineQueryServiceImpl implements MachineQueryService {
    
    private final MachineRepository machineRepository;
    
    public MachineQueryServiceImpl(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }
    
    @Override
    public List<Machine> handle(GetAllMachinesQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting all machines for tenant {}", tenantId);
        return machineRepository.findAllByTenantId(tenantId);
    }
    
    @Override
    public List<Machine> handle(GetAvailableMachinesQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting available machines for tenant {}", tenantId);
        return machineRepository.findAvailableByTenantId(tenantId);
    }
    
    @Override
    public List<Machine> handle(GetMachinesDueForMaintenanceQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting machines due for maintenance for tenant {}", tenantId);
        return machineRepository.findMachinesDueForMaintenanceByTenantId(tenantId);
    }
    
    @Override
    public Optional<Machine> handle(GetMachineByIdQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting machine by ID {} for tenant {}", query.machineId(), tenantId);
        return machineRepository.findByIdAndTenantId(query.machineId(), tenantId);
    }
    
    @Override
    public Optional<Machine> handle(GetMachineBySerialNumberQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting machine by serial number {} for tenant {}", query.serialNumber(), tenantId);
        return machineRepository.findBySerialNumberAndTenantId(query.serialNumber(), tenantId);
    }
    
    @Override
    public List<Machine> handle(GetMachinesByProductionLineQuery query) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        log.info("Getting machines by production line {} for tenant {}", query.productionLineId(), tenantId);
        return machineRepository.findByProductionLineIdAndTenantId(query.productionLineId(), tenantId);
    }
}