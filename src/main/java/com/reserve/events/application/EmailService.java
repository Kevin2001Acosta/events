package com.reserve.events.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de correos electrónicos con soporte para adjuntos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envía un correo electrónico con un archivo PDF adjunto de forma asíncrona.
     *
     * @param to          Correo del destinatario
     * @param subject     Asunto del correo
     * @param body        Cuerpo del mensaje (puede ser HTML)
     * @param pdfBytes    Contenido del PDF en bytes
     * @param pdfFileName Nombre del archivo PDF adjunto
     */
    @Async
    public void sendEmailWithPdfAttachment(String to, String subject, String body,
                                           byte[] pdfBytes, String pdfFileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            // Adjuntar el PDF
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfBytes), "application/pdf");

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", to);
        } catch (MessagingException e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error al enviar el correo electrónico", e);
        }
    }

    /**
     * Envía un correo electrónico simple (sin adjuntos).
     *
     * @param to      Correo del destinatario
     * @param subject Asunto del correo
     * @param body    Cuerpo del mensaje (puede ser HTML)
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", to);
        } catch (MessagingException e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error al enviar el correo electrónico", e);
        }
    }
}

