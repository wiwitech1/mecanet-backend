package com.wiwitech.mecanetbackend.conditionmonitoring.infrastructure.adapters.asset;

import com.wiwitech.mecanetbackend.conditionmonitoring.application.internal.outboundservices.MachineCatalogAcl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Simple REST client that checks the AssetManagement API to verify
 * the existence of a machine for the current tenant.
 */
@Component
public class MachineCatalogAclRestClient implements MachineCatalogAcl {

    private static final Logger LOG = LoggerFactory.getLogger(MachineCatalogAclRestClient.class);
    private final RestClient rest;

    /** Base URL can be externalized in application.properties */
    public MachineCatalogAclRestClient(RestClient.Builder builder) {
        this.rest = builder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public boolean machineExists(Long machineId, Long tenantId) {
        try {
            var response = rest.get()
                               .uri("/api/v1/machines/{id}", machineId)
                               .header("X-Tenant-ID", String.valueOf(tenantId))   // optional tenant header
                               .retrieve()
                               .toBodilessEntity();
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception ex) {
            LOG.warn("ACL validation failed for machine {}: {}", machineId, ex.getMessage());
            return false;
        }
    }
}