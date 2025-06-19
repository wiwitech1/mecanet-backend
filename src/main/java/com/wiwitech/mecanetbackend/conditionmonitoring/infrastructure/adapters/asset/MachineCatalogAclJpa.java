package com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.adapters.asset;
import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.MachineRepository;
import com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.outboundservices.MachineCatalogAcl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary   // será la implementación preferida
@Component
public class MachineCatalogAclJpa implements MachineCatalogAcl {

    private final MachineRepository machineRepository;

    public MachineCatalogAclJpa(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public boolean machineExists(Long machineId, Long tenantId) {
        return machineRepository.findByIdAndTenantId(machineId, tenantId).isPresent();
    }
}