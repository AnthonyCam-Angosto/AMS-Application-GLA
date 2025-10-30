package com.analyse_crypto.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityApiTests {
    @Autowired
    private MockMvc mockMvc;

    //@Test
    @WithMockUser(username="user", roles={"USER"})
    void accessDeniedForUser() throws Exception {
        mockMvc.perform(get("/admin"))
               .andExpect(status().isForbidden());
    }
}
