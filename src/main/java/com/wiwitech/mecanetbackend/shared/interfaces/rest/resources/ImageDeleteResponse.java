package com.wiwitech.mecanetbackend.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ImageDeleteResponse(
    String publicId,
    String result,
    LocalDateTime deletedAt
) {
    
    public static ImageDeleteResponse fromCloudinaryResult(java.util.Map<String, Object> result) {
        return new ImageDeleteResponse(
            (String) result.get("public_id"),
            (String) result.get("result"),
            LocalDateTime.now()
        );
    }
} 