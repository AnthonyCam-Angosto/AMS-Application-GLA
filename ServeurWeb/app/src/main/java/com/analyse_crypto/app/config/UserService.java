package com.analyse_crypto.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.analyse_crypto.app.config.exception.EmailAlreadyUsedException;
import com.analyse_crypto.app.config.exception.IdentifiantAlreadyUsedException;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void verificationUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyUsedException("Email deja utilise: "+user.getEmail());
        }
        if(userRepository.existsByIdentifiant(user.getIdentifiant())){
            throw new IdentifiantAlreadyUsedException("identifiant deja utilise: "+user.getIdentifiant());
        }
    }

    public User ajouterUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
