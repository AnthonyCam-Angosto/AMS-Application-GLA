package com.analyse_crypto.app.tables.utilisateurs;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entité représentant un portefeuille d'un utilisateur.
 *
 * Un portefeuille appartient à un `User` et possède un nom unique.
 */
@Entity
@Table(
    name = "portefeuilles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nom"})
    }
)
public class Portefeuille {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_portefeuille")
    private Long idPortefeuille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "nom", nullable = false, length = 100, unique = true)
    private String nom;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    public Portefeuille() {}

    public Portefeuille(User user, String nom, LocalDateTime dateCreation) {
        this.user = user;
        this.nom = nom;
        this.dateCreation = dateCreation;
    }

    public Long getIdPortefeuille() {
        return idPortefeuille;
    }

    public void setIdPortefeuille(Long idPortefeuille) {
        this.idPortefeuille = idPortefeuille;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
