package com.analyse_crypto.app.tables.data_crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;


@DataJpaTest
public class CryptoTest {
    @Autowired
    private CryptoRepository cryptoRepository;

    @Test
    void testBDCrypto() {
        Crypto crypto=new Crypto("BTC","Bitcoin");
        Crypto saved = cryptoRepository.save(crypto);

        assertNotNull(saved.getId());
        assertEquals(cryptoRepository.count(), 1);
        assertEquals(saved.getSymbole(), "BTC");
        assertEquals(saved.getNom(),"Bitcoin");
    }

    @Test
    void testfind(){
        Crypto crypto=new Crypto("BTC","Bitcoin");
        cryptoRepository.save(crypto);

        Crypto result=cryptoRepository.findBySymbole("BTC");
        assertNotNull(result.getId());
        assertEquals(result.getSymbole(), "BTC");
    }


}
