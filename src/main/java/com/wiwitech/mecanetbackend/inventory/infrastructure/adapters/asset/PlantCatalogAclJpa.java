package com.wiwitech.mecanetbackend.inventory.infrastructure.adapters.asset;

import com.wiwitech.mecanetbackend.assetmanagment.infrastructure.persistence.jpa.repositories.PlantRepository;
import com.wiwitech.mecanetbackend.inventory.application.internal.outboundservices.PlantCatalogAcl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para consultar plantas del BC AssetManagement
 * Implementación preferida para el BC Inventory que usa acceso directo a la base de datos
 */
@Primary
@Component("inventoryPlantCatalogAclJpa")
public class PlantCatalogAclJpa implements PlantCatalogAcl {

    private static final Logger LOG = LoggerFactory.getLogger(PlantCatalogAclJpa.class);
    private final PlantRepository plantRepository;

    public PlantCatalogAclJpa(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    public boolean plantExists(Long plantId, Long tenantId) {
        LOG.debug("Validating plant existence: plantId={}, tenantId={}", plantId, tenantId);
        boolean exists = plantRepository.findByIdAndTenantId(plantId, tenantId).isPresent();
        LOG.debug("Plant validation result: plantId={}, exists={}", plantId, exists);
        return exists;
    }

    @Override
    public String getPlantName(Long plantId, Long tenantId) {
        LOG.debug("Retrieving plant name: plantId={}, tenantId={}", plantId, tenantId);
        String plantName = plantRepository.findByIdAndTenantId(plantId, tenantId)
            .map(plant -> plant.getName())
            .orElse(null);
        LOG.debug("Plant name retrieved: plantId={}, name={}", plantId, plantName);
        return plantName;
    }
} 