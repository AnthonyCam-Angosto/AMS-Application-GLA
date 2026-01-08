package com.analyse_crypto.app.tables.data_crypto.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;

public interface CryptoHistoryRepository extends JpaRepository<CryptoHistory, Long> {
    List<CryptoHistory> findByCrypto(Crypto crypto);
    List<CryptoHistory> findByCryptoAndDateTimeBetween(Crypto crypto, LocalDateTime start, LocalDateTime end);
    CryptoHistory findTopByCryptoOrderByDateTimeDesc(Crypto crypto);

}