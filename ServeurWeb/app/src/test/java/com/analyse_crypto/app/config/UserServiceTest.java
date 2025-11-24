package com.analyse_crypto.app.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.analyse_crypto.app.config.exception.EmailAlreadyUsedException;
import com.analyse_crypto.app.config.exception.IdentifiantAlreadyUsedException;
import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    UserService userService;

    @Test
    void verificationUserTest_normal(){
        User user=new User("user", "user@google.com", "user", RoleUser.UTILISATEUR);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByIdentifiant(user.getIdentifiant())).thenReturn(false);

        assertDoesNotThrow(() -> userService.verificationUser(user));
    }

    @Test
    void verificationUserTest_erreurEmail(){
        User user=new User("user", "user@google.com", "user", RoleUser.UTILISATEUR);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> {
            userService.verificationUser(user);
        });
    }

    @Test
    void verificationUserTest_erreurPassword(){
        User user=new User("user", "user@google.com", "user", RoleUser.UTILISATEUR);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByIdentifiant(user.getIdentifiant())).thenReturn(true);

        assertThrows(IdentifiantAlreadyUsedException.class, () -> {
            userService.verificationUser(user);
        });
    }
}
