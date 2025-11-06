package com.analyse_crypto.app.tables.utilisateurs;

import java.math.BigDecimal;

import com.analyse_crypto.app.tables.data_crypto.Crypto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comptes")
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compte")
    private Long idCompte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_portefeuille", nullable = false)
    private Portefeuille portefeuille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id", nullable = false)
    private Crypto crypto;

    @Column(name = "solde", nullable = false, precision = 20, scale = 10)
    private BigDecimal solde;

    public Compte() {}

    public Compte(Portefeuille portefeuille, Crypto crypto, BigDecimal solde) {
        this.portefeuille = portefeuille;
        this.crypto = crypto;
        this.solde = solde;
    }

    public Long getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(Long idCompte) {
        this.idCompte = idCompte;
    }

    public Portefeuille getPortefeuille() {
        return portefeuille;
    }

    public void setPortefeuille(Portefeuille portefeuille) {
        this.portefeuille = portefeuille;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }
}
