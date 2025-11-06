package com.analyse_crypto.app.tables.data_crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.data_crypto.Crypto;

public interface  CryptoRepository  extends JpaRepository<Crypto, Long>{
    
    Crypto findBySymbole(String symbole);
}
