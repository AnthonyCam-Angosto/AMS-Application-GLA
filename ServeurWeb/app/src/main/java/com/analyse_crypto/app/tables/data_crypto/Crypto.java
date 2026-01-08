package com.analyse_crypto.app.tables.data_crypto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entité représentant une cryptomonnaie référencée dans l'application.
 *
 * Contient son identifiant interne, le symbole et le nom lisible.
 */
@Entity
@Table(name="cryptos")
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="crypto_id")
    private Long id;

    @Column(name = "symbole", nullable = false, unique = true, length = 5)
    private String symbole;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    public Crypto() {}

    public Crypto(String symbole, String nom) {
        this.symbole = symbole;
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public String getSymbole() {
        return symbole;
    }

    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }
    
}
