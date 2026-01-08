package com.analyse_crypto.app.controlleur;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.alerts.repository.AlertRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@ExtendWith(MockitoExtension.class)
public class AlertsControllerTest {

    @Mock
    CryptoRepository cryptoRepo;

    @Mock
    AlertRepository alertRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AlertsController controller;

    @Test
    void showAlerts_populates_model_and_returns_page() {
        when(cryptoRepo.findAllSymbole()).thenReturn(List.of("BTCUSDT"));
        Model model = new ExtendedModelMap();

        String page = controller.showAlerts(model);

        assertEquals("alerts", page);
        assertTrue(model.containsAttribute("AlertForm"));
        assertEquals(List.of("BTCUSDT"), model.asMap().get("cryptos"));
    }

    @Test
    void createAlert_returns_page_with_validation_error_when_binding_has_errors() {
        when(cryptoRepo.findAllSymbole()).thenReturn(List.of("BTCUSDT"));
        AlertsController.AlertForm form = new AlertsController.AlertForm();
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(true);

        Model model = new ExtendedModelMap();
        String page = controller.createAlert(form, br, model, null);

        assertEquals("alerts", page);
        assertEquals("Veuillez corriger les champs du formulaire.", model.asMap().get("errorMsg"));
    }

    @Test
    void createAlert_returns_page_when_unauthenticated() {
        when(cryptoRepo.findAllSymbole()).thenReturn(List.of("BTCUSDT"));
        AlertsController.AlertForm form = new AlertsController.AlertForm();
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        Model model = new ExtendedModelMap();
        String page = controller.createAlert(form, br, model, null);

        assertEquals("alerts", page);
        assertEquals("Utilisateur non authentifié.", model.asMap().get("errorMsg"));
    }

    @Test
    void createAlert_returns_page_when_user_not_found() {
        when(cryptoRepo.findAllSymbole()).thenReturn(List.of("BTCUSDT"));
        AlertsController.AlertForm form = new AlertsController.AlertForm();
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unknown");
        when(userRepository.findByIdentifiant("unknown")).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String page = controller.createAlert(form, br, model, auth);

        assertEquals("alerts", page);
        assertEquals("Utilisateur introuvable.", model.asMap().get("errorMsg"));
    }

    @Test
    void createAlert_returns_page_when_save_fails() {
        when(cryptoRepo.findAllSymbole()).thenReturn(List.of("BTCUSDT"));
        AlertsController.AlertForm form = new AlertsController.AlertForm();
        form.setAsset("BTCUSDT");
        form.setCondition(">");
        form.setPrice(new BigDecimal("100"));
        form.setMethod("email");
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("tester");
        User user = new User();
        user.setIdentifiant("tester");
        when(userRepository.findByIdentifiant("tester")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("DB")).when(alertRepository).save(any());

        Model model = new ExtendedModelMap();
        String page = controller.createAlert(form, br, model, auth);

        assertEquals("alerts", page);
        assertEquals("Impossible de sauvegarder l'alerte.", model.asMap().get("errorMsg"));
    }

    @Test
    void createAlert_success_saves_and_returns_success_message() {
        when(cryptoRepo.findAllSymbole()).thenReturn(List.of("BTCUSDT"));
        AlertsController.AlertForm form = new AlertsController.AlertForm();
        form.setAsset("BTCUSDT");
        form.setCondition(">");
        form.setPrice(new BigDecimal("100"));
        form.setMethod("email");
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("tester");
        User user = new User();
        user.setIdentifiant("tester");
        when(userRepository.findByIdentifiant("tester")).thenReturn(Optional.of(user));

        Model model = new ExtendedModelMap();
        String page = controller.createAlert(form, br, model, auth);

        assertEquals("alerts", page);
        assertEquals("Alerte créée avec succès pour " + form.getAsset(), model.asMap().get("successMsg"));
        assertTrue(model.containsAttribute("AlertForm"));
        verify(alertRepository, times(1)).save(any());
    }
}