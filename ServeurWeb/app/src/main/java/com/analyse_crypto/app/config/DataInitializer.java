package com.analyse_crypto.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@Configuration
public class DataInitializer {

     @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            if (repo.findByIdentifiant("admin").isEmpty()) {
                User admin = new User();
                admin.setIdentifiant("admin");
                admin.setPassword(encoder.encode("admin"));
                admin.setRole(RoleUser.ADMIN);
                admin.setEmail("test@google.com");
                repo.save(admin);
                System.out.println("Compte admin créé");
            }
        };
    }
}
