package com.analyse_crypto.app.controlleur;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.analyse_crypto.app.tables.alerts.Alert;
import com.analyse_crypto.app.tables.alerts.repository.AlertRepository;
import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

@Controller
public class AlertsController {

    @Autowired
    CryptoRepository cryptoRepo;
    
    @Autowired
    AlertRepository alertRepository;

    @Autowired
    UserRepository userRepository;

    private final String page = "alerts";

    public static class AlertForm {
        @NotBlank
        private String asset;

        @NotBlank
        private String condition;

        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal price;

        @NotBlank
        private String method;

        private boolean active = true;

        public String getAsset() { return asset; }
        public void setAsset(String asset) { this.asset = asset; }

        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    @GetMapping("/alerts")
    public String showAlerts(Model model){
        model.addAttribute("AlertForm", new AlertForm());
        try {
            model.addAttribute("cryptos", cryptoRepo.findAllSymbole());
        } catch (Exception e) {
            model.addAttribute("cryptos", java.util.Arrays.asList("BTCUSDT","ETHUSDT"));
        }
        return page;
    }

    @PostMapping("/alerts")
    public String createAlert(@Valid @ModelAttribute("AlertForm") AlertForm form, BindingResult result, Model model, Authentication authentication){
        model.addAttribute("AlertForm", form);
        try {
            model.addAttribute("cryptos", cryptoRepo.findAllSymbole());
        } catch (Exception e) {
            model.addAttribute("cryptos", java.util.Arrays.asList("BTCUSDT","ETHUSDT"));
        }

        if(result.hasErrors()){
            model.addAttribute("errorMsg", "Veuillez corriger les champs du formulaire.");
            return page;
        }

        if (authentication == null || authentication.getName() == null) {
            model.addAttribute("errorMsg", "Utilisateur non authentifié.");
            return page;
        }

        User user = null;
        try {
            user = userRepository.findByIdentifiant(authentication.getName()).orElse(null);
        } catch (Exception e) { }

        if (user == null) {
            model.addAttribute("errorMsg", "Utilisateur introuvable.");
            return page;
        }

        Alert a = new Alert();
        a.setUser(user);
        a.setAsset(form.getAsset());
        a.setCondition(form.getCondition());
        a.setPrice(form.getPrice());
        a.setMethod(form.getMethod());
        a.setActive(form.isActive());

        try {
            alertRepository.save(a);
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Impossible de sauvegarder l'alerte.");
            return page;
        }

        model.addAttribute("successMsg", "Alerte créée avec succès pour " + form.getAsset());
        model.addAttribute("AlertForm", new AlertForm());
        return page;
    }

}
