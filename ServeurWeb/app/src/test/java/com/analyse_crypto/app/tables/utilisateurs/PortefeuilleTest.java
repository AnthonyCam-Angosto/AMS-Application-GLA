package com.analyse_crypto.app.tables.utilisateurs;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.repository.PortefeuilleRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@DataJpaTest
public class PortefeuilleTest {

    @Autowired
    private PortefeuilleRepository repo;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private final LocalDateTime date=LocalDateTime.of(2025, 11, 1, 10, 15);

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        userRepository.deleteAll();

        user= new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        userRepository.save(user);
    }

    @Test
    void testPortefeuille(){
        Portefeuille portefeuille= new Portefeuille(user, "exemple", date);
        Portefeuille saved = repo.save(portefeuille);

        assertNotNull(saved.getIdPortefeuille());
        assertEquals(repo.count(), 1);
        assertEquals(saved.getUser(),user);
        assertEquals(saved.getNom(),"exemple");
        assertEquals(saved.getDateCreation(),date);

        saved.setIdPortefeuille(Long.valueOf("5"));
        assertEquals(saved.getIdPortefeuille(),Long.valueOf("5"));
        User temp_user= new User("test2", "test@google.com", "test", RoleUser.UTILISATEUR);
        saved.setUser(temp_user);
        assertEquals(saved.getUser(),temp_user);
        saved.setNom("test");
        assertEquals(saved.getNom(),"test");
        LocalDateTime temp_date=LocalDateTime.of(2024, 11, 1, 10, 15);
        saved.setDateCreation(temp_date);
        assertEquals(saved.getDateCreation(),temp_date);
    }

    @Test
    void testfind(){
       Portefeuille portefeuille= new Portefeuille(user, "exemple", date);
       repo.save(portefeuille); 

       List<Portefeuille> result=repo.findByUser(user);
       assertEquals(result.size(), 1);
       assertEquals(result.get(0).getUser(), user);
    }
    
}
