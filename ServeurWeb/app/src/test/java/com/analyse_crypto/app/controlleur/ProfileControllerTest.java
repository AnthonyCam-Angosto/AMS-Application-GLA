package com.analyse_crypto.app.controlleur;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.analyse_crypto.app.config.CustomUserDetails;
import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void test_profile_Page() throws Exception {
        User user=new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);

        mockMvc.perform(get("/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(customUser)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("profile"));
    }

    @Test
    void test_change_email() throws Exception {
        User user=new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);
        when(userRepository.findByIdentifiant(user.getIdentifiant())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/profile/change-email")
                        .with(csrf())
                        .param("newEmail", "user@google.com")
                        .with(user(customUser)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("profile"))
                        .andExpect(model().attribute("successMsg", "Email mis à jour avec succès."));
    }

    @Test
    void test_change_email_erreur() throws Exception {
        User user=new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);
        when(userRepository.findByIdentifiant(user.getIdentifiant())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/profile/change-email")
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(customUser))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("newEmail", user.getEmail()))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeHasFieldErrors("EmailChangeForm", "newEmail"));
    }

    @Test
    void test_change_email_exist() throws Exception {
        User user=new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);
        when(userRepository.findByIdentifiant(user.getIdentifiant())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/profile/change-email")
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(customUser))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("newEmail", user.getEmail()))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeHasFieldErrors("EmailChangeForm", "newEmail"));
    }

    @Test
    void test_change_email_null() throws Exception {
        User user=new User("test", "test@google.com", "test", RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);

        mockMvc.perform(post("/profile/change-email")
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(customUser))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("newEmail",""))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeHasFieldErrors("EmailChangeForm", "newEmail"));
    }

    @Test
    void test_change_password() throws Exception {
        String password=passwordEncoder.encode("test");
        User user=new User("test", "test@google.com", password, RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);
        when(userRepository.findByIdentifiant(user.getIdentifiant())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/profile/change-password")
                        .with(csrf())
                        .param("oldPassword", "test")
                        .param("newPassword", "user")
                        .with(user(customUser)))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("successMsg", "Mot de passe mis à jour avec succès."));
    }

    @Test
    void test_change_password_exist() throws Exception {
        String password=passwordEncoder.encode("test");
        User user=new User("test", "test@google.com", password, RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);
        when(userRepository.findByIdentifiant(user.getIdentifiant())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/profile/change-password")
                        .with(csrf())
                        .param("oldPassword", "eee")
                        .param("newPassword", "val")
                        .with(user(customUser)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("profile"))
                        .andExpect(model().attributeHasFieldErrors("PasswordChangeForm", "oldPassword"));
    }

    @Test
    void test_change_password_null() throws Exception {
        String password=passwordEncoder.encode("test");
        User user=new User("test", "test@google.com", password, RoleUser.UTILISATEUR);
        CustomUserDetails customUser=new CustomUserDetails(user);

        mockMvc.perform(post("/profile/change-password")
                        .with(csrf())
                        .param("oldPassword", "test")
                        .param("newPassword", "")
                        .with(user(customUser)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("profile"));
    }
}
