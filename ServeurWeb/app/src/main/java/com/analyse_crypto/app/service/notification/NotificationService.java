package com.analyse_crypto.app.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.analyse_crypto.app.tables.alerts.Alert;
import com.analyse_crypto.app.tables.utilisateurs.User;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void notifyUser(User user, Alert alert, String message) {
        String method = alert.getMethod() == null ? "email" : alert.getMethod();

        if ("email" .equalsIgnoreCase(method) && user.getEmail() != null) {
            if (mailSender != null) {
                try {
                    SimpleMailMessage msg = new SimpleMailMessage();
                    msg.setTo(user.getEmail());
                    msg.setSubject("CryptoView - Alerte : " + alert.getAsset());
                    msg.setText(message + "\n\n(Envoyé par CryptoView)");
                    mailSender.send(msg);
                    System.out.println("[NOTIFICATION] Email envoyé à=" + user.getEmail());
                    return;
                } catch (Exception e) {
                    System.err.println("Erreur envoi email: " + e.getMessage());
                }
            } else {
                System.out.println("[NOTIFICATION] JavaMailSender non configuré — fallback console. to=" + user.getEmail());
            }
        }
    }
}
