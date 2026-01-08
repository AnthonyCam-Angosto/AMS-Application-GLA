package com.analyse_crypto.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@Service
public class CryptoService {

    @Autowired
    CryptoRepository cryptoRepo;

    @Autowired
    CryptoHistoryRepository cryptoHistoryRepo;
    
    public double getPrice(String cryptoId) {
        Crypto crypto = cryptoRepo.findBySymbole(cryptoId);
        CryptoHistory cryptoHistory = cryptoHistoryRepo.findTopByCryptoOrderByDateTimeDesc(crypto);
        return cryptoHistory.getClosePrice().doubleValue();
    }
}
