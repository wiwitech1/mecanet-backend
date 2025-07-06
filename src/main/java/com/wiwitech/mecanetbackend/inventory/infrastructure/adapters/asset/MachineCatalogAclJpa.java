package com.wiwitech.mecanetbackend.inventory.infrastructure.adapters.asset;

import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.MachineRepository;
import com.wiwitech.mecanetbackend.inventory.application.internal.outboundservices.MachineCatalogAcl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para consultar máquinas del BC AssetManagement
 * Implementación preferida para el BC Inventory que usa acceso directo a la base de datos
 */
@Primary
@Component("inventoryMachineCatalogAclJpa")
public class MachineCatalogAclJpa implements MachineCatalogAcl {

    private static final Logger LOG = LoggerFactory.getLogger(MachineCatalogAclJpa.class);
    private final MachineRepository machineRepository;

    public MachineCatalogAclJpa(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public boolean machineExists(Long machineId, Long tenantId) {
        LOG.debug("Validating machine existence: machineId={}, tenantId={}", machineId, tenantId);
        boolean exists = machineRepository.findByIdAndTenantId(machineId, tenantId).isPresent();
        LOG.debug("Machine validation result: machineId={}, exists={}", machineId, exists);
        return exists;
    }

    @Override
    public String getMachineName(Long machineId, Long tenantId) {
        LOG.debug("Retrieving machine name: machineId={}, tenantId={}", machineId, tenantId);
        String machineName = machineRepository.findByIdAndTenantId(machineId, tenantId)
            .map(machine -> machine.getName())
            .orElse(null);
        LOG.debug("Machine name retrieved: machineId={}, name={}", machineId, machineName);
        return machineName;
    }

    @Override
    public boolean isMachineCompatible(Long machineId, String itemSku, Long tenantId) {
        LOG.debug("Checking machine compatibility: machineId={}, itemSku={}, tenantId={}", machineId, itemSku, tenantId);
        // Por ahora, todas las máquinas son compatibles con todos los items
        // En el futuro, esto podría implementarse con una tabla de compatibilidad
        boolean compatible = machineExists(machineId, tenantId);
        LOG.debug("Machine compatibility result: machineId={}, itemSku={}, compatible={}", machineId, itemSku, compatible);
        return compatible;
    }
} 