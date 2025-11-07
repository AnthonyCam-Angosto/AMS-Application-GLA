package com.analyse_crypto.app.tables.utilisateurs;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@DataJpaTest
public class UserTest {

    @Autowired
    private UserRepository repo;

    @Test
    void testUser(){
        User user= new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        User saved = repo.save(user);

        assertNotNull(saved.getUserId());
        assertEquals(repo.count(), 1);
        assertEquals(saved.getIdentifiant(),"test");
        assertEquals(saved.getRole(),RoleUser.UTILISATEUR);
    }

    @Test
    void testfind(){
        User user= new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        repo.save(user);

        Optional<User> result=repo.findByIdentifiant("test");
        assertTrue(result.isPresent());
        assertEquals(result.get().getIdentifiant(), "test");

        result=repo.findByIdentifiant("aaa");
        assertFalse(result.isPresent());
    }

}
