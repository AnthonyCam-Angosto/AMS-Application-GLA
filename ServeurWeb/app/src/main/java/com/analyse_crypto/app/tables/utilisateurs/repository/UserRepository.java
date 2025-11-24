package com.analyse_crypto.app.tables.utilisateurs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.utilisateurs.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdentifiant(String identifiant);
    boolean existsByEmail(String email);
    boolean existsByIdentifiant(String identifiant);
}
