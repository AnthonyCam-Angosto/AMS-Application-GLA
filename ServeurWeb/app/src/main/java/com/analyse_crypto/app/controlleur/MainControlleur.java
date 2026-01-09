package com.analyse_crypto.app.controlleur;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.service.CryptoService;

/**
 * Contrôleur principal exposant la page de tableau de bord.
 *
 * Fournit les endpoints web liés à l'affichage du dashboard.
 */
@Controller
public class MainControlleur {

    @Autowired
    CryptoRepository cryptoRepo;

    @Autowired
    CryptoService cryptoService;

    @GetMapping("/cryptos")
    @ResponseBody
    public List<String> getCryptos(){
        List<String> cryptos = cryptoRepo.findAllSymbole();
        return cryptos;
    }

    @GetMapping("/price")
    @ResponseBody
    public double getPrice(@RequestParam String crypto) {
        double price = cryptoService.getPrice(crypto);
        return price;
    }


    /**
     * Endpoint GET pour afficher la page du dashboard.
     *
     * Récupère la liste des symboles de cryptomonnaies et les place
     * dans le modèle pour rendu côté vue.
     */
    @GetMapping("/dashboard")
    public String getDashboard(Model model){
        List<String> cryptos = cryptoRepo.findAllSymbole();
        model.addAttribute("cryptos", cryptos);
        return "dashboard";
    }

    /**
     * Endpoint GET pour afficher la page du forecast.
     *
     * Récupère la liste des symboles de cryptomonnaies et les place
     * dans le modèle pour rendu côté vue.
     */
    @GetMapping("/forecast")
    public String getForecast(Model model){
        List<String> cryptos = cryptoRepo.findAllSymbole();
        model.addAttribute("cryptos", cryptos);
        return "forecast";
    }

    /**
     * Endpoint GET pour afficher la page du portfolio.
     */
    @GetMapping("/portfolio")
    public String afficherPortfolio() {
        return "portfolio";
    }
}
