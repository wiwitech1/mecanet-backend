package com.wiwitech.mecanetbackend.shared.infrastructure.services;

import com.wiwitech.mecanetbackend.shared.domain.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SpringMailEmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public SpringMailEmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendHtmlEmail(String to, String title, String description, String buttonText, String buttonUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(title);
            
            // Crear modelo para el template
            Map<String, Object> model = new HashMap<>();
            model.put("title", title);
            model.put("description", description);
            model.put("buttonText", buttonText);
            model.put("buttonUrl", buttonUrl);
            
            Context context = new Context();
            model.forEach(context::setVariable);
            
            String htmlContent = templateEngine.process("email-template", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email HTML enviado exitosamente a: {} con título: {}", to, title);
            
        } catch (MessagingException e) {
            log.error("Error al enviar email HTML a: {}", to, e);
            throw new RuntimeException("Error al enviar email HTML", e);
        }
    }
}