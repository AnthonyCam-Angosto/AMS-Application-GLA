package com.analyse_crypto.app.tables.alerts;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.User;

public class AlertTest {

    @Test
    void alertGettersSetters() {
        Alert alert = new Alert();
        User user = new User("ident", "u@ex.com", "pwd", RoleUser.UTILISATEUR);
        alert.setUser(user);
        alert.setAsset("BTC");
        alert.setCondition(">");
        alert.setPrice(new BigDecimal("12345.678"));
        alert.setMethod("email");
        alert.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        alert.setCreatedAt(now);

        assertEquals(user, alert.getUser());
        assertEquals("BTC", alert.getAsset());
        assertEquals(">", alert.getCondition());
        assertEquals(new BigDecimal("12345.678"), alert.getPrice());
        assertEquals("email", alert.getMethod());
        assertTrue(alert.isActive());
        assertEquals(now, alert.getCreatedAt());
    }
}
