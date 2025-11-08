package com.analyse_crypto.app.tables.utilisateurs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.CompteRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.PortefeuilleRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@DataJpaTest
public class CompteTest {

    @Autowired
    private CompteRepository repo;
    
    @Autowired
    private PortefeuilleRepository portefeuilleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CryptoRepository cryptoRepository;

    private User user;
    private Portefeuille portefeuille;
    private Crypto crypto;
    private LocalDateTime date=LocalDateTime.of(2025, 11, 1, 10, 15);

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        portefeuilleRepository.deleteAll();
        userRepository.deleteAll();


        user= new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        userRepository.save(user);
        portefeuille= new Portefeuille(user, "exemple", date);
        portefeuilleRepository.save(portefeuille);

        crypto=new Crypto("BTC","Bitcoin");
        cryptoRepository.save(crypto);
    }

    @Test
    void testCompte(){
        BigDecimal val=new BigDecimal(10);
        Compte compte= new Compte(portefeuille, crypto, val);
        Compte saved = repo.save(compte);

        assertNotNull(saved.getIdCompte());
        assertEquals(repo.count(), 1);
        assertEquals(saved.getPortefeuille(),portefeuille);
        assertEquals(saved.getCrypto(),crypto);
        assertEquals(saved.getSolde(),val);
    }

    @Test
    void testfind(){
       Compte compte= new Compte(portefeuille, crypto, new BigDecimal(10));
       repo.save(compte); 

       List<Compte> result=repo.findByPortefeuille(portefeuille);
       assertEquals(result.size(), 1);
       assertEquals(result.get(0).getPortefeuille(), portefeuille);
    }
}
