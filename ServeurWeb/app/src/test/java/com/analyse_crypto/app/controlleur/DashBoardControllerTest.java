package com.analyse_crypto.app.controlleur;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.time.LocalDateTime;
import java.util.List;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.CryptoHistory;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoHistoryRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class DashBoardControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    CryptoHistoryRepository cryptoHistoRepo;

    @MockitoBean
    CryptoRepository cryptoRepo;


    @Test
    void test_getOhlc() throws Exception{
        Crypto btc = new Crypto("BTC","bitcoin");

        BigDecimal val=new BigDecimal(100);
        CryptoHistory history = new CryptoHistory(btc,LocalDateTime.now(),val,val,val,val,val);

        when(cryptoRepo.findBySymbole("BTC")).thenReturn(btc);
        when(cryptoHistoRepo.findByCryptoAndDateTimeBetween(eq(btc), any(), any())).thenReturn(List.of(history));

        mockMvc.perform(get("/dashboard/ohlc")
                        .param("range", "7")
                        .param("typeC", "BTC"))
               .andExpect(status().isOk());

              // .andExpect(jsonPath("$[0].open").value(val))
              // .andExpect(jsonPath("$[0].close").value(val))
              // .andExpect(jsonPath("$[0].high").value(val))
             //  .andExpect(jsonPath("$[0].low").value(val));
    }
}
