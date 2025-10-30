package com.analyse_crypto.app.controlleur;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestMain {
    
    @GetMapping("/ping")
    public String ping(){
        return "ping";
    }
}
