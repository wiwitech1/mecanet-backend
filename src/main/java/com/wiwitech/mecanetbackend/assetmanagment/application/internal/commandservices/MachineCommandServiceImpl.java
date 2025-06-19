package com.wiwitech.mecanetbackend.assetmanagment.application.internal.commandservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import com.wiwitech.mecanetbackend.assetmanagment.domain.model.aggregates.Machine;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.AssignMachineToProductionLineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.CompleteMachineMaintenanceCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.RegisterMachineCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.commands.StartMachineMaintenanceCommand;
import com.wiwitech.mecanetbackend.assetmanagment.domain.services.MachineCommandService;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.MachineRepository;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.ProductionLineRepository;
import com.wiwitech.mecanetbackend.shared.domain.model.valueobjects.TenantId;
import com.wiwitech.mecanetbackend.shared.infrastructure.multitenancy.TenantContext;
import com.wiwitech.mecanetbackend.assetmanagment.domain.model.events.MachineCreatedEvent;

/**
 * Machine command service implementation
 * Handles machine command operations with multitenancy support
 */
@Service
public class MachineCommandServiceImpl implements MachineCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(MachineCommandServiceImpl.class);
    
    private final MachineRepository machineRepository;
    private final ProductionLineRepository productionLineRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public MachineCommandServiceImpl(MachineRepository machineRepository, 
                                   ProductionLineRepository productionLineRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.machineRepository = machineRepository;
        this.productionLineRepository = productionLineRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public Machine handle(RegisterMachineCommand command) {
        logger.info("Registering new machine: {}", command.serialNumber());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Validar que serialNumber no exista en el tenant
        if (machineRepository.existsBySerialNumberAndTenantId(command.serialNumber(), tenantId)) {
            throw new RuntimeException("Machine with serial number already exists in this organization");
        }
        
        Machine machine = Machine.createNew(
            command.serialNumber(),
            command.name(),
            command.specs(),
            new TenantId(tenantId)
        );
        
        Machine savedMachine = machineRepository.save(machine);
        logger.info("Machine registered successfully with ID: {}", savedMachine.getId());
        
        // ---- publish domain event for other bounded-contexts ----
        eventPublisher.publishEvent(new MachineCreatedEvent(savedMachine.getId(), tenantId));
        // ---------------------------------------------------------

        return savedMachine;
    }
    
    @Override
    @Transactional
    public Machine handle(AssignMachineToProductionLineCommand command) {
        logger.info("Assigning machine {} to production line {}", 
                   command.machineId(), command.productionLineId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        // Buscar la máquina en el tenant actual
        Machine machine = machineRepository.findByIdAndTenantId(command.machineId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        // Validar que la línea de producción existe en el mismo tenant
        productionLineRepository.findByIdAndTenantId(command.productionLineId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Production line not found"));
        
        // Asignar la máquina
        machine.assignToProductionLine(command.productionLineId());
        
        Machine savedMachine = machineRepository.save(machine);
        logger.info("Machine assigned successfully to production line {}", command.productionLineId());
        
        return savedMachine;
    }
    
    @Override
    @Transactional
    public Machine handle(StartMachineMaintenanceCommand command) {
        logger.info("Starting maintenance for machine {}", command.machineId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        Machine machine = machineRepository.findByIdAndTenantId(command.machineId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        machine.startMaintenance();
        
        Machine savedMachine = machineRepository.save(machine);
        logger.info("Maintenance started successfully for machine {}", machine.getSerialNumber());
        
        return savedMachine;
    }
    
    @Override
    @Transactional
    public Machine handle(CompleteMachineMaintenanceCommand command) {
        logger.info("Completing maintenance for machine {}", command.machineId());
        
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        
        Machine machine = machineRepository.findByIdAndTenantId(command.machineId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        machine.completeMaintenance();
        
        Machine savedMachine = machineRepository.save(machine);
        logger.info("Maintenance completed successfully for machine {}", machine.getSerialNumber());
        
        return savedMachine;
    }
}