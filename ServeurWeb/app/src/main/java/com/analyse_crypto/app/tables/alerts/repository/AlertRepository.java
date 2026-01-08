package com.analyse_crypto.app.tables.alerts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.alerts.Alert;
import com.analyse_crypto.app.tables.utilisateurs.User;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUser(User user);
    List<Alert> findByActiveTrue();
}
