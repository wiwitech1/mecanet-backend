package com.wiwitech.mecanetbackend.shared.infrastructure.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.wiwitech.mecanetbackend.shared.domain.services.ImageUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryImageUploadServiceImpl implements ImageUploadService {

    private final Cloudinary cloudinary;

    public CloudinaryImageUploadServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
        try {
            log.info("Subiendo imagen: {} al folder: {}", file.getOriginalFilename(), folder);
            
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto",
                "quality", "auto",
                "fetch_format", "auto"
            );

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            
            log.info("Imagen subida exitosamente. Public ID: {}", result.get("public_id"));
            return result;
            
        } catch (IOException e) {
            log.error("Error al subir imagen: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "mecanet");
    }

    @Override
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        try {
            log.info("Eliminando imagen con public_id: {}", publicId);
            
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            
            log.info("Imagen eliminada exitosamente. Public ID: {}", publicId);
            return result;
            
        } catch (IOException e) {
            log.error("Error al eliminar imagen con public_id: {}", publicId, e);
            throw e;
        }
    }
} 