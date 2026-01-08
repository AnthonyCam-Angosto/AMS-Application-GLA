package com.analyse_crypto.app.controlleur;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.analyse_crypto.app.service.alerts.AlertService;
import com.analyse_crypto.app.service.notification.NotificationService;
import com.analyse_crypto.app.service.CryptoService;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class MainControlleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoRepository cryptoRepo;

    @MockBean
    private CryptoService cryptoService;

    @MockBean 
    private AlertService alertService; 

    @MockBean 
    private NotificationService notificationService;

    @Test
    void test_Dashboard_Page() throws Exception {
        ArrayList<String> val=new ArrayList();
        val.add("test");
        when(cryptoRepo.findAllSymbole()).thenReturn(val);
        mockMvc.perform(get("/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard"));
    }
    
    
    @Test
    void test_ForecastForecast_Page() throws Exception {
        ArrayList<String> val=new ArrayList();
        val.add("test");
        when(cryptoRepo.findAllSymbole()).thenReturn(val);
        mockMvc.perform(get("/forecast"))
               .andExpect(status().isFound());
    }
}
