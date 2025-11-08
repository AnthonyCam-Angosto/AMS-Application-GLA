package com.analyse_crypto.app.tables.data_crypto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@DataJpaTest
public class CryptoHistoryTest {
    @Autowired
    private CryptoHistoryRepository repo;

    @Autowired
    private CryptoRepository cryptoRepository;

    private Crypto crypto;
    private LocalDateTime date=LocalDateTime.of(2025, 11, 1, 10, 15);

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        cryptoRepository.deleteAll();

        crypto=new Crypto("BTC","Bitcoin");
        cryptoRepository.save(crypto);
    }


    @Test
    void testBDCrypto() {
        CryptoHistory cryptohisto=new CryptoHistory(crypto,date,BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45));
        CryptoHistory saved = repo.save(cryptohisto);

        assertNotNull(saved.getId());
        assertEquals(repo.count(), 1);
        assertEquals(saved.getCrypto(), crypto);
        assertEquals(saved.getHighPrice(),BigDecimal.valueOf(123.45));
        assertEquals(saved.getClosePrice(),BigDecimal.valueOf(123.45));
        assertEquals(saved.getLowPrice(),BigDecimal.valueOf(123.45));
        assertEquals(saved.getOpenPrice(),BigDecimal.valueOf(123.45));
        assertEquals(saved.getDateTime(),date);
    }

    @Test
    void testfind(){
        CryptoHistory cryptohisto=new CryptoHistory(crypto,date,BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45),BigDecimal.valueOf(123.45));
        repo.save(cryptohisto);

        List<CryptoHistory> result=repo.findByCrypto(crypto);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getCrypto(), crypto);

        LocalDateTime min=LocalDateTime.of(2025, 10, 1, 10, 15);
        LocalDateTime max=LocalDateTime.of(2025, 12, 1, 10, 15);
        result=repo.findByCryptoAndDateTimeBetween(crypto, min, max);
        assertEquals(result.get(0).getCrypto(), crypto);
    }
}
