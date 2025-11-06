package com.analyse_crypto.app.tables.utilisateurs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.utilisateurs.Compte;
import com.analyse_crypto.app.tables.utilisateurs.Portefeuille;

public interface CompteRepository extends JpaRepository<Compte, Long> {
    List<Compte> findByPortefeuille(Portefeuille portefeuille);
    List<Compte> findByCrypto(Crypto crypto);
}
