package com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.outboundservices;
/**
 * Anti-corruption layer that allows ConditionMonitoring to ask
 * AssetManagement if a machine exists for the current tenant.
 */
public interface MachineCatalogAcl {
    boolean machineExists(Long machineId, Long tenantId);
}