package com.analyse_crypto.app.controlleur;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@RestController
@RequestMapping("/dashboard/")
public class DashboardController {

    @Autowired
    CryptoHistoryRepository cryptoHistoRepo;

    @Autowired
    CryptoRepository cryptoRepo;

    @GetMapping("/ohlc")
    public List<CryptoHistory> getOhlc(@RequestParam(defaultValue = "30") int range,@RequestParam(defaultValue = "BTC") String typeC) {
        Crypto crypto=cryptoRepo.findBySymbole(typeC);

        LocalDateTime end=LocalDateTime.now();
        LocalDateTime start=end.minusDays(range);

        List<CryptoHistory> ohlc = cryptoHistoRepo.findByCryptoAndDateTimeBetween(crypto, start, end);
        return ohlc;
    }
}