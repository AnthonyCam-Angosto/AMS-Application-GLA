package com.analyse_crypto.app.tables.data_crypto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.analyse_crypto.app.tables.data_crypto.Crypto;

public interface  CryptoRepository  extends JpaRepository<Crypto, Long>{
    
    Crypto findBySymbole(String symbole);

    @Query("SELECT c.symbole FROM Crypto c")
    List<String> findAllSymbole();
}
