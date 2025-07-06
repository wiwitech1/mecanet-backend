package com.wiwitech.mecanetbackend.inventory.application.internal.outboundservices;

/**
 * Anti-corruption layer que permite a Inventory consultar
 * AssetManagement si una planta existe para el tenant actual.
 */
public interface PlantCatalogAcl {
    
    /**
     * Verifica si una planta existe para el tenant especificado
     * 
     * @param plantId ID de la planta
     * @param tenantId ID del tenant
     * @return true si la planta existe, false en caso contrario
     */
    boolean plantExists(Long plantId, Long tenantId);
    
    /**
     * Obtiene el nombre de una planta
     * 
     * @param plantId ID de la planta
     * @param tenantId ID del tenant
     * @return nombre de la planta o null si no existe
     */
    String getPlantName(Long plantId, Long tenantId);
} 