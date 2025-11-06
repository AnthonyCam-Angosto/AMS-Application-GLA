package com.analyse_crypto.app.tables.utilisateurs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.TypeTransac;
import com.analyse_crypto.app.tables.utilisateurs.Compte;
import com.analyse_crypto.app.tables.utilisateurs.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompte(Compte compte);
    List<Transaction> findByType(TypeTransac type);
}
