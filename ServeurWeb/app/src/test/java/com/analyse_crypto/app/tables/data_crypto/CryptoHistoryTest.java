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
    private final LocalDateTime date=LocalDateTime.of(2025, 11, 1, 10, 15);
    private final BigDecimal val=BigDecimal.valueOf(123.45);

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        cryptoRepository.deleteAll();

        crypto=new Crypto("BTC","Bitcoin");
        cryptoRepository.save(crypto);
    }


    @Test
    void testBDCrypto() {
        CryptoHistory cryptohisto=new CryptoHistory(crypto,date,val,val,val,val,val);
        CryptoHistory saved = repo.save(cryptohisto);
        saved.setVolume(val);
        saved=repo.save(saved);

        assertNotNull(saved.getId());
        assertEquals(repo.count(), 1);
        assertEquals(saved.getCrypto(), crypto);
        assertEquals(saved.getHighPrice(),val);
        assertEquals(saved.getClosePrice(),val);
        assertEquals(saved.getLowPrice(),val);
        assertEquals(saved.getOpenPrice(),val);
        assertEquals(saved.getDateTime(),date);
        assertEquals(saved.getVolume(),val);

        saved.setId(Long.valueOf("5"));
        assertEquals(saved.getId(),Long.valueOf("5"));
        LocalDateTime temp_date=LocalDateTime.of(2024, 11, 1, 10, 15);
        saved.setDateTime(temp_date);
        assertEquals(saved.getDateTime(),temp_date);

        BigDecimal temp_val=BigDecimal.valueOf(100.45);
        saved.setHighPrice(temp_val);
        assertEquals(saved.getHighPrice(),temp_val);
        saved.setClosePrice(temp_val);
        assertEquals(saved.getClosePrice(),temp_val);
        saved.setLowPrice(temp_val);
        assertEquals(saved.getLowPrice(),temp_val);
        saved.setOpenPrice(temp_val);
        assertEquals(saved.getOpenPrice(),temp_val);
    }

    @Test
    void testfind(){
        CryptoHistory cryptohisto=new CryptoHistory(crypto,date,val,val,val,val,val);
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
