package com.analyse_crypto.app.service.notification;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.alerts.Alert;
import com.analyse_crypto.app.tables.utilisateurs.User;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void notifyUser_sendsEmailWhenConfigured() {
        User user = new User("ident", "dest@example.com", "pwd", RoleUser.UTILISATEUR);
        Alert alert = new Alert();
        alert.setMethod("email");
        alert.setAsset("BTC");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        assertDoesNotThrow(() -> notificationService.notifyUser(user, alert, "Test message"));

        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        // verify destination and subject contain asset
        org.junit.jupiter.api.Assertions.assertEquals("dest@example.com", sent.getTo()[0]);
        org.junit.jupiter.api.Assertions.assertTrue(sent.getSubject().contains("BTC"));
    }

    @Test
    void notifyUser_noEmailWhenUserHasNoEmail() {
        User user = new User("ident", null, "pwd", RoleUser.UTILISATEUR);
        Alert alert = new Alert();
        alert.setMethod("email");
        alert.setAsset("ETH");

        // mailSender should not be used when user has no email
        notificationService.notifyUser(user, alert, "Message");

        verifyNoInteractions(mailSender);
    }
}
