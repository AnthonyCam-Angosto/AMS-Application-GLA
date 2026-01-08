package com.analyse_crypto.app.tables.utilisateurs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.utilisateurs.Portefeuille;
import com.analyse_crypto.app.tables.utilisateurs.User;

public interface PortefeuilleRepository extends JpaRepository<Portefeuille, Long> {
    List<Portefeuille> findByUser(User user);
    Portefeuille findByIdPortefeuilleAndUser(int id, User user);
}
