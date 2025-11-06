package com.analyse_crypto.app.tables.utilisateurs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.utilisateurs.Portefeuille;
import com.analyse_crypto.app.tables.utilisateurs.User;

public interface PortefeuilleRepository extends JpaRepository<Portefeuille, Long> {
    Optional<Portefeuille> findByNom(String nom);
    List<Portefeuille> findByUser(User user);
}
