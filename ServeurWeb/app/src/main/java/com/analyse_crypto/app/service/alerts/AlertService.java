package com.analyse_crypto.app.service.alerts;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.analyse_crypto.app.service.notification.NotificationService;
import com.analyse_crypto.app.tables.alerts.Alert;
import com.analyse_crypto.app.tables.alerts.repository.AlertRepository;
import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@Service
public class AlertService {

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    CryptoRepository cryptoRepository;

    @Autowired
    CryptoHistoryRepository cryptoHistoryRepository;

    @Autowired
    NotificationService notificationService;

    // Vérifie les alertes toutes les 1 heures
    @Scheduled(fixedDelayString = "${alerts.check.delay:3600000}")
    public void checkAlerts() {
        List<Alert> alerts = alertRepository.findByActiveTrue();
        for (Alert a : alerts) {
            try {
                Crypto c = cryptoRepository.findBySymbole(a.getAsset());
                if (c == null) continue;
                List<CryptoHistory> hist = cryptoHistoryRepository.findByCrypto(c);
                if (hist == null || hist.isEmpty()) continue;

                CryptoHistory latest = hist.stream().max((h1,h2)->h1.getDateTime().compareTo(h2.getDateTime())).get();
                BigDecimal price = latest.getClosePrice();

                boolean triggered = false;
                switch (a.getCondition()) {
                    case ">": triggered = price.compareTo(a.getPrice()) > 0; break;
                    case "<": triggered = price.compareTo(a.getPrice()) < 0; break;
                    case "=": triggered = price.compareTo(a.getPrice()) == 0; break;
                }

                if (triggered) {
                    notificationService.notifyUser(a.getUser(), a, "Alerte déclenchée pour " + a.getAsset() + " prix=" + price);
                    a.setActive(false);
                    alertRepository.save(a);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la vérification des alertes : " + e.getMessage());
            }
        }
    }
}
