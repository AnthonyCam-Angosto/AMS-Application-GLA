package com.analyse_crypto.app.controlleur;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;

@Controller
public class MainControlleur {

    @Autowired
    CryptoRepository cryptoRepo;
    

    @GetMapping("/dashboard")
    public String getDashboard(Model model){
        List<String> cryptos=cryptoRepo.findAllSymbole();
        model.addAttribute("cryptos",cryptos);
        return "dashboard";
    }
}
