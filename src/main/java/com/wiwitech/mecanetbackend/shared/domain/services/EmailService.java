package com.wiwitech.mecanetbackend.shared.domain.services;

public interface EmailService {
    
    /**
     * Envía un email HTML con diseño corporativo fijo
     * 
     * @param to Destinatario del email
     * @param title Título del email
     * @param description Descripción del contenido
     * @param buttonText Texto del botón
     * @param buttonUrl URL del botón
     */
    void sendHtmlEmail(String to, String title, String description, String buttonText, String buttonUrl);
}
