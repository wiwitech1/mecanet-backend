package com.wiwitech.mecanetbackend.shared.domain.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ImageUploadService {
    
    /**
     * Sube una imagen a Cloudinary y retorna la información de la imagen
     * 
     * @param file El archivo de imagen a subir
     * @param folder El folder donde se guardará la imagen (opcional)
     * @return Map con la información de la imagen subida (url, public_id, etc.)
     * @throws IOException Si hay error al procesar el archivo
     */
    Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException;
    
    /**
     * Sube una imagen a Cloudinary en el folder raíz
     * 
     * @param file El archivo de imagen a subir
     * @return Map con la información de la imagen subida
     * @throws IOException Si hay error al procesar el archivo
     */
    Map<String, Object> uploadImage(MultipartFile file) throws IOException;
    
    /**
     * Elimina una imagen de Cloudinary por su public_id
     * 
     * @param publicId El public_id de la imagen a eliminar
     * @return Map con el resultado de la eliminación
     * @throws IOException Si hay error al eliminar
     */
    Map<String, Object> deleteImage(String publicId) throws IOException;
} 