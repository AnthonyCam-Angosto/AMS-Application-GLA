package com.analyse_crypto.app.controlleur;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class MainControlleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CryptoRepository cryptoRepo;

    @Test
    void test_Dashboard_Page() throws Exception {
        ArrayList<String> val=new ArrayList();
        val.add("test");
        when(cryptoRepo.findAllSymbole()).thenReturn(val);
        mockMvc.perform(get("/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard"));
    }   
}
