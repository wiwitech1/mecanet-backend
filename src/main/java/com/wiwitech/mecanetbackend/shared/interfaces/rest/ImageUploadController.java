package com.wiwitech.mecanetbackend.shared.interfaces.rest;

import com.wiwitech.mecanetbackend.shared.domain.services.ImageUploadService;
import com.wiwitech.mecanetbackend.shared.interfaces.rest.resources.ImageDeleteResponse;
import com.wiwitech.mecanetbackend.shared.interfaces.rest.resources.ImageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Image Upload", description = "API para subida y gestión de imágenes con Cloudinary")
@Slf4j
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    public ImageUploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Subir imagen",
        description = "Sube una imagen a Cloudinary y retorna la información de la imagen subida"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Imagen subida exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ImageUploadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Archivo inválido o error en la subida"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<ImageUploadResponse> uploadImage(
        @Parameter(description = "Archivo de imagen a subir", required = true)
        @RequestParam("file") MultipartFile file,
        
        @Parameter(description = "Folder donde guardar la imagen (opcional)")
        @RequestParam(value = "folder", required = false, defaultValue = "mecanet") String folder
    ) {
        try {
            log.info("Recibida solicitud de subida de imagen: {} al folder: {}", 
                    file.getOriginalFilename(), folder);

            // Validar archivo
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            // Subir imagen
            Map<String, Object> result = imageUploadService.uploadImage(file, folder);
            
            // Convertir respuesta
            ImageUploadResponse response = ImageUploadResponse.fromCloudinaryResult(result);
            
            log.info("Imagen subida exitosamente. Public ID: {}", response.publicId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            log.error("Error al subir imagen: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{publicId}")
    @Operation(
        summary = "Eliminar imagen",
        description = "Elimina una imagen de Cloudinary por su public_id"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Imagen eliminada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ImageDeleteResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Imagen no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<ImageDeleteResponse> deleteImage(
        @Parameter(description = "Public ID de la imagen a eliminar", required = true)
        @PathVariable String publicId
    ) {
        try {
            log.info("Recibida solicitud de eliminación de imagen con public_id: {}", publicId);

            Map<String, Object> result = imageUploadService.deleteImage(publicId);
            
            ImageDeleteResponse response = ImageDeleteResponse.fromCloudinaryResult(result);
            
            log.info("Imagen eliminada exitosamente. Public ID: {}", publicId);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error al eliminar imagen con public_id: {}", publicId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 