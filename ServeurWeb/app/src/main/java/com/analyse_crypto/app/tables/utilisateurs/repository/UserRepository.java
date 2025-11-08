package com.analyse_crypto.app.tables.utilisateurs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.analyse_crypto.app.tables.utilisateurs.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdentifiant(String identifiant);
    boolean existsByEmail(String email);
    boolean existsByIdentifiant(String identifiant);

    /*
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :email WHERE u.identifiant= :identifiant")
    void updateEmail(@Param("identifiant") String identifiant,@Param("email") String email);
    */
}
