package com.analyse_crypto.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void endpoint_public() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void authentification() throws Exception {
        mockMvc.perform(get("/profile"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void non_autoriser_user() throws Exception {
        mockMvc.perform(get("/admin/menu"))
               .andExpect(status().isForbidden());
    }

    @Test
    void test_login() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }


}
