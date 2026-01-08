package com.analyse_crypto.app.controlleur;

import com.analyse_crypto.app.tables.data_crypto.Crypto;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.tables.utilisateurs.Compte;
import com.analyse_crypto.app.tables.utilisateurs.Portefeuille;
import com.analyse_crypto.app.tables.utilisateurs.Transaction;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.CompteRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.PortefeuilleRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PortefeuilleRepository portefeuilleRepository;

    @MockBean
    private CompteRepository compteRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private CryptoRepository cryptoRepository;

    private User fakeUser() {
        User u = new User();
        u.setIdentifiant("test");
        u.setEmail("test@google.com");
        return u;
    }

    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testListPortfolios() throws Exception {
        Portefeuille p = new Portefeuille(fakeUser(), "Test", LocalDateTime.now());
        Mockito.when(portefeuilleRepository.findByUser(any())).thenReturn(List.of(p));

        mockMvc.perform(get("/portfolio/list")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Test"));
    }

    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testGetComptesOk() throws Exception {
        Portefeuille p = new Portefeuille(fakeUser(), "Test", LocalDateTime.now());
        Mockito.when(portefeuilleRepository.findByIdPortefeuilleAndUser(eq(1), any())).thenReturn(p);

        Compte c = new Compte();
        c.setSolde(BigDecimal.TEN);
        Mockito.when(compteRepository.findByPortefeuille(p)).thenReturn(List.of(c));

        mockMvc.perform(get("/portfolio/1/comptes")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].solde").value(10));
    }

    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testGetComptesNotFound() throws Exception {
        Mockito.when(portefeuilleRepository.findByIdPortefeuilleAndUser(eq(1), any())).thenReturn(null);

        mockMvc.perform(get("/portfolio/1/comptes")
                        .principal(() -> "test"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testGetTransactions() throws Exception {
        Portefeuille p = new Portefeuille(fakeUser(), "Test", LocalDateTime.now());
        Mockito.when(portefeuilleRepository.findByIdPortefeuilleAndUser(eq(1), any())).thenReturn(p);

        Compte c = new Compte();
        Mockito.when(compteRepository.findByPortefeuille(p)).thenReturn(List.of(c));

        Transaction t = new Transaction();
        t.setMontant(BigDecimal.ONE);
        Mockito.when(transactionRepository.findByCompteIn(List.of(c))).thenReturn(List.of(t));

        mockMvc.perform(get("/portfolio/1/transactions")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].montant").value(1));
    }

    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testAddTransaction() throws Exception {
        PortfolioController.TransactionRequest req = new PortfolioController.TransactionRequest();

        req.setId_portefeuille(1);
        req.setCrypto_id("BTC");
        req.setType("ENTREE");
        req.setMontant(BigDecimal.valueOf(100));

        Portefeuille p = new Portefeuille(fakeUser(), "Test", LocalDateTime.now());
        Mockito.when(portefeuilleRepository.findByIdPortefeuilleAndUser(eq(1), any())).thenReturn(p);

        Compte c = new Compte();
        Mockito.when(compteRepository.findByPortefeuilleAndCrypto_Symbole(p, "BTC")).thenReturn(c);

        mockMvc.perform(post("/portfolio/transaction")
                        .with(csrf()) 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .principal(() -> "test"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testAddTransactionPortfolioNotFound() throws Exception {
        PortfolioController.TransactionRequest req = new PortfolioController.TransactionRequest();
        req.setId_portefeuille(1);

        Mockito.when(portefeuilleRepository.findByIdPortefeuilleAndUser(eq(1), any())).thenReturn(null);

        mockMvc.perform(post("/portfolio/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .principal(() -> "test"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username="user", roles={"UTILISATEUR"})
    void testCreatePortfolio() throws Exception {
        PortfolioController.CreatePortfolioRequest req = new PortfolioController.CreatePortfolioRequest();
        req.setNom("MonPortefeuille");
        req.setCrypto_id("BTC");

        Crypto crypto = new Crypto();
        crypto.setSymbole("BTC");

        Mockito.when(cryptoRepository.findBySymbole("BTC")).thenReturn(crypto);

        mockMvc.perform(post("/portfolio/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("MonPortefeuille"));
    }
}
