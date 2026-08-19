package com.ufide.biblioapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// si no hay correo configurado, el link se muestra en la consola para poder probar
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:no-reply@biblioapp.local}")
    private String remitente;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void enviarLinkRecuperacion(String destinatario, String link) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            // como no hay correo configurado, dejo el link en el log
            log.warn("[DEMO] spring.mail.* no configurado. Link de recuperacion para {}: {}",
                    destinatario, link);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("BiblioApp - Restablecer contrasena");
        mensaje.setText("Recibimos un pedido para restablecer tu contrasena.\n\n"
                + "Abri este enlace para elegir una nueva (vence en 1 hora):\n" + link + "\n\n"
                + "Si no fuiste vos, ignora este correo.");
        mailSender.send(mensaje);
        log.info("Correo de recuperacion enviado a {}", destinatario);
    }
}
