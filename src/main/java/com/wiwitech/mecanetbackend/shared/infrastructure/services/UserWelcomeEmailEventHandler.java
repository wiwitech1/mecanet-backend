package com.wiwitech.mecanetbackend.shared.infrastructure.services;

import com.wiwitech.mecanetbackend.iam.domain.model.events.UserCreatedEvent;
import com.wiwitech.mecanetbackend.shared.domain.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event Handler que envía email de bienvenida automáticamente
 * cuando se crea cualquier usuario (admin o técnico)
 */
@Component("sharedUserWelcomeEmailEventHandler")
@Slf4j
public class UserWelcomeEmailEventHandler {

    private final EmailService emailService;

    public UserWelcomeEmailEventHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Maneja el evento UserCreatedEvent para enviar email de bienvenida
     * Se ejecuta para TODOS los usuarios: admin y técnicos
     */
    @EventListener
    public void on(UserCreatedEvent event) {
        try {
            log.info("Enviando email de bienvenida para usuario: {} ({})", 
                    event.username(), event.email());

            // Determinar el tipo de mensaje según el rol
            String title, description, buttonText, buttonUrl;
            
            if (event.roles().contains("ROLE_ADMIN")) {
                // Email para admin (creador del tenant)
                title = "¡Bienvenido a Mecanet!";
                description = String.format(
                    "Hola %s, tu cuenta de administrador ha sido creada exitosamente. " +
                    "Ya puedes gestionar usuarios, activos industriales y configurar tu organización.",
                    event.firstName()
                );
                buttonText = "Acceder al Dashboard";
                buttonUrl = "https://mecanet.wiwitech.com/admin/dashboard";
                
            } else {
                // Email para técnicos y otros usuarios
                title = "¡Bienvenido a Mecanet!";
                description = String.format(
                    "Hola %s, tu cuenta ha sido creada exitosamente. " +
                    "Ya puedes acceder al sistema de gestión industrial Mecanet.",
                    event.firstName()
                );
                buttonText = "Acceder al Sistema";
                buttonUrl = "https://mecanet.wiwitech.com/login";
            }

            // Enviar email de bienvenida
            emailService.sendHtmlEmail(
                event.email(),
                title,
                description,
                buttonText,
                buttonUrl
            );

            log.info("Email de bienvenida enviado exitosamente a: {}", event.email());

        } catch (Exception e) {
            log.error("Error al enviar email de bienvenida para usuario: {} - Error: {}", 
                     event.username(), e.getMessage(), e);
            // NO relanzar la excepción para no interrumpir el flujo principal
        }
    }
} 