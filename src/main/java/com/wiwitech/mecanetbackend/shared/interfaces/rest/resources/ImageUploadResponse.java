package com.wiwitech.mecanetbackend.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ImageUploadResponse(
    String publicId,
    String url,
    String secureUrl,
    String format,
    Long bytes,
    Integer width,
    Integer height,
    String resourceType,
    LocalDateTime uploadedAt
) {
    
    public static ImageUploadResponse fromCloudinaryResult(java.util.Map<String, Object> result) {
        return new ImageUploadResponse(
            (String) result.get("public_id"),
            (String) result.get("url"),
            (String) result.get("secure_url"),
            (String) result.get("format"),
            result.get("bytes") != null ? ((Number) result.get("bytes")).longValue() : null,
            result.get("width") != null ? ((Number) result.get("width")).intValue() : null,
            result.get("height") != null ? ((Number) result.get("height")).intValue() : null,
            (String) result.get("resource_type"),
            LocalDateTime.now()
        );
    }
} 