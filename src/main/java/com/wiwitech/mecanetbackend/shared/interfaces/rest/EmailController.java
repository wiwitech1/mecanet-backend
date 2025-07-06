package com.wiwitech.mecanetbackend.shared.interfaces.rest;

import com.wiwitech.mecanetbackend.shared.domain.services.EmailService;
import com.wiwitech.mecanetbackend.shared.interfaces.rest.resources.EmailRequest;
import com.wiwitech.mecanetbackend.shared.interfaces.rest.resources.EmailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/email")
@Tag(name = "Email Service", description = "API para envío de correos electrónicos")
@Slf4j
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @Operation(summary = "Enviar email HTML", description = "Envía un email HTML con diseño corporativo")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest request) {
        try {
            log.info("Recibida solicitud de envío de email a: {}", request.to());

            emailService.sendHtmlEmail(
                request.to(), 
                request.title(), 
                request.description(), 
                request.buttonText(), 
                request.buttonUrl()
            );

            EmailResponse response = new EmailResponse("Email enviado exitosamente", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.error("Error al enviar email", e);
            EmailResponse response = new EmailResponse("Error al enviar email: " + e.getMessage(), LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}