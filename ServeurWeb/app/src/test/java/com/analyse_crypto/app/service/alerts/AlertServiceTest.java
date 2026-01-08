package com.analyse_crypto.app.service.alerts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.analyse_crypto.app.service.notification.NotificationService;
import com.analyse_crypto.app.tables.alerts.Alert;
import com.analyse_crypto.app.tables.alerts.repository.AlertRepository;
import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.tables.utilisateurs.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    AlertRepository alertRepository;

    @Mock
    CryptoRepository cryptoRepository;

    @Mock
    CryptoHistoryRepository cryptoHistoryRepository;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    AlertService alertService;

    private Alert makeAlert(String asset, String condition, String price) {
        Alert a = new Alert();
        a.setAsset(asset);
        a.setCondition(condition);
        a.setPrice(new BigDecimal(price));
        a.setActive(true);
        User u = new User(); // assuming a default constructor exists
        u.setIdentifiant("tester");
        a.setUser(u);
        return a;
    }

    private Crypto makeCrypto(String symbole) {
        Crypto c = new Crypto();
        c.setSymbole(symbole);
        return c;
    }

    private CryptoHistory makeHistory(Crypto crypto, String closePrice, LocalDateTime dt) {
        CryptoHistory h = new CryptoHistory();
        h.setCrypto(crypto);
        h.setClosePrice(new BigDecimal(closePrice));
        h.setDateTime(dt);
        return h;
    }

    @Test
    void latestSelection_triggers_when_latest_meets_condition() {
        Alert alert = makeAlert("BTC", ">", "100");
        Crypto crypto = makeCrypto("BTC");

        CryptoHistory older = makeHistory(crypto, "90", LocalDateTime.of(2023,1,1,0,0));
        CryptoHistory newer = makeHistory(crypto, "110", LocalDateTime.of(2023,1,1,1,0));

        when(alertRepository.findByActiveTrue()).thenReturn(List.of(alert));
        when(cryptoRepository.findBySymbole("BTC")).thenReturn(crypto);
        when(cryptoHistoryRepository.findByCrypto(crypto)).thenReturn(List.of(older, newer));

        alertService.checkAlerts();

        // notification called with user and alert, message contains latest price 110
        verify(notificationService, times(1)).notifyUser(eq(alert.getUser()), eq(alert), argThat(msg -> msg.contains("prix=110")));
        // alert should be deactivated and saved
        ArgumentCaptor<Alert> cap = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(cap.capture());
        Alert saved = cap.getValue();
        assert !saved.isActive();
    }

    @Test
    void latestSelection_does_not_trigger_if_latest_does_not_meet_even_if_older_did() {
        Alert alert = makeAlert("ETH", ">", "150");
        Crypto crypto = makeCrypto("ETH");

        CryptoHistory older = makeHistory(crypto, "200", LocalDateTime.of(2023,1,1,0,0));
        CryptoHistory newer = makeHistory(crypto, "100", LocalDateTime.of(2023,1,1,1,0));

        when(alertRepository.findByActiveTrue()).thenReturn(List.of(alert));
        when(cryptoRepository.findBySymbole("ETH")).thenReturn(crypto);
        when(cryptoHistoryRepository.findByCrypto(crypto)).thenReturn(List.of(older, newer));

        alertService.checkAlerts();

        // notification should NOT be called because latest (100) does not satisfy ">" 150
        verify(notificationService, never()).notifyUser(any(), any(), any());
        verify(alertRepository, never()).save(any());
        assert alert.isActive();
    }

    @Test
    void latestSelection_triggers_on_equal_condition() {
        Alert alert = makeAlert("XRP", "=", "2.50");
        Crypto crypto = makeCrypto("XRP");

        CryptoHistory older = makeHistory(crypto, "2.00", LocalDateTime.of(2023,1,1,0,0));
        CryptoHistory newer = makeHistory(crypto, "2.50", LocalDateTime.of(2023,1,1,1,0));

        when(alertRepository.findByActiveTrue()).thenReturn(List.of(alert));
        when(cryptoRepository.findBySymbole("XRP")).thenReturn(crypto);
        when(cryptoHistoryRepository.findByCrypto(crypto)).thenReturn(List.of(older, newer));

        alertService.checkAlerts();

        verify(notificationService, times(1)).notifyUser(eq(alert.getUser()), eq(alert), argThat(msg -> msg.contains("prix=2.50")));
        ArgumentCaptor<Alert> cap = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(cap.capture());
        assert !cap.getValue().isActive();
    }
}
