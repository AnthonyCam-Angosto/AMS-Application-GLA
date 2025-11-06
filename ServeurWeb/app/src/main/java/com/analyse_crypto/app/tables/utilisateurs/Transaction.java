package com.analyse_crypto.app.tables.utilisateurs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.analyse_crypto.app.tables.TypeTransac;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long idTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeTransac type;

    @Column(name = "montant", nullable = false, precision = 20, scale = 10)
    private BigDecimal montant;

    @Column(name = "date_transaction", nullable = false)
    private LocalDateTime dateTransaction;


    public Transaction() {}

    public Transaction(Compte compte, TypeTransac type, BigDecimal montant, LocalDateTime dateTransaction) {
        this.compte = compte;
        this.type = type;
        this.montant = montant;
        this.dateTransaction = dateTransaction;
    }

    public Long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public TypeTransac getType() {
        return type;
    }

    public void setType(TypeTransac type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
}