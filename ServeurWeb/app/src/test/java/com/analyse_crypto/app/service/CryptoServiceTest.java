package com.analyse_crypto.app.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@ExtendWith(MockitoExtension.class)
class CryptoServiceTest {

    @Mock
    private CryptoRepository cryptoRepo;

    @Mock
    private CryptoHistoryRepository cryptoHistoryRepo;

    @InjectMocks
    private CryptoService cryptoService;

    @Test
    void test_getPrice_normal() {
        Crypto btc = new Crypto();
        btc.setSymbole("BTC");

        CryptoHistory lastHistory = new CryptoHistory();
        lastHistory.setClosePrice(BigDecimal.valueOf(42000.55));

        when(cryptoRepo.findBySymbole("BTC")).thenReturn(btc);
        when(cryptoHistoryRepo.findTopByCryptoOrderByDateTimeDesc(btc))
                .thenReturn(lastHistory);

        double price = cryptoService.getPrice("BTC");

        assertEquals(42000.55, price);
        verify(cryptoRepo, times(1)).findBySymbole("BTC");
        verify(cryptoHistoryRepo, times(1)).findTopByCryptoOrderByDateTimeDesc(btc);
    }
}
