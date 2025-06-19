package com.wiwitech.mecanetbackend.iam.domain.model.events;

import java.util.List;

/**
 * Evento de dominio que se emite al crear cualquier usuario dentro de un tenant.
 */
public record UserCreatedEvent(
        Long userId,
        String username,
        String firstName,
        String lastName,
        String email,
        Long tenantId,
        List<String> roles        // valores “ROLE_xxx”
) { }