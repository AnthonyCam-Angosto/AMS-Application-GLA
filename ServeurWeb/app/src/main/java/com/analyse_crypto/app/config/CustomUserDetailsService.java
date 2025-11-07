package com.analyse_crypto.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByIdentifiant(username).orElseThrow(()->new UsernameNotFoundException("Utilisateur non trouvé"));
        
        return org.springframework.security.core.userdetails.User
                  .withUsername(user.getIdentifiant())
                  .password(user.getPassword())
                  .roles(user.getRole().toString())
                  .build();
    }
}
