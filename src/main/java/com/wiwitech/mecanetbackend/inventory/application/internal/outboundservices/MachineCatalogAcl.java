package com.wiwitech.mecanetbackend.inventory.application.internal.outboundservices;

/**
 * Anti-corruption layer que permite a Inventory consultar
 * AssetManagement si una máquina existe para el tenant actual.
 */
public interface MachineCatalogAcl {
    
    /**
     * Verifica si una máquina existe para el tenant especificado
     * 
     * @param machineId ID de la máquina
     * @param tenantId ID del tenant
     * @return true si la máquina existe, false en caso contrario
     */
    boolean machineExists(Long machineId, Long tenantId);
    
    /**
     * Obtiene el nombre de una máquina
     * 
     * @param machineId ID de la máquina
     * @param tenantId ID del tenant
     * @return nombre de la máquina o null si no existe
     */
    String getMachineName(Long machineId, Long tenantId);
    
    /**
     * Verifica si una máquina es compatible con un item específico
     * 
     * @param machineId ID de la máquina
     * @param itemSku SKU del item de inventario
     * @param tenantId ID del tenant
     * @return true si la máquina es compatible, false en caso contrario
     */
    boolean isMachineCompatible(Long machineId, String itemSku, Long tenantId);
} 