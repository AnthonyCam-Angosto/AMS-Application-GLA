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
import com.analyse_crypto.app.tables.TypeTransac;
import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.CompteRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.PortefeuilleRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.TransactionRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@DataJpaTest
public class TransactionTest {

    @Autowired
    private TransactionRepository repo;
    
    @Autowired
    private CompteRepository compteRepository;
    
    @Autowired
    private PortefeuilleRepository portefeuilleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CryptoRepository cryptoRepository;

    private User user;
    private Portefeuille portefeuille;
    private Crypto crypto;
    private Compte compte;
    private LocalDateTime date=LocalDateTime.of(2025, 11, 1, 10, 15);

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        compteRepository.deleteAll();
        portefeuilleRepository.deleteAll();
        userRepository.deleteAll();


        user= new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        userRepository.save(user);
        portefeuille= new Portefeuille(user, "exemple", date);
        portefeuilleRepository.save(portefeuille);

        crypto=new Crypto("BTC","Bitcoin");
        cryptoRepository.save(crypto);

        compte= new Compte(portefeuille, crypto, new BigDecimal(10));
        compteRepository.save(compte);
    }

    @Test
    void testTransaction(){
        BigDecimal val=new BigDecimal(10);
        Transaction transaction= new Transaction(compte, TypeTransac.ENTREE,val, date);
        Transaction saved = repo.save(transaction);

        assertNotNull(saved.getIdTransaction());
        assertEquals(repo.count(), 1);
        assertEquals(saved.getCompte(),compte);
        assertEquals(saved.getType(),TypeTransac.ENTREE);
        assertEquals(saved.getDateTransaction(),date);
        assertEquals(saved.getMontant(),val);
    }

    @Test
    void testfind(){
        Transaction transaction= new Transaction(compte, TypeTransac.ENTREE, new BigDecimal(10), date);
        repo.save(transaction); 

       List<Transaction> result=repo.findByCompte(compte);
       assertEquals(result.size(), 1);
       assertEquals(result.get(0).getType(), TypeTransac.ENTREE);
    }
}
