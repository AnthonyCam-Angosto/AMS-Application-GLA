package com.analyse_crypto.app.controlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analyse_crypto.app.config.UserService;
import com.analyse_crypto.app.tables.RoleUser;
import com.analyse_crypto.app.tables.utilisateurs.User;

@Controller
public class InscriptionController {

    @Autowired
    UserService userService;

    @GetMapping("/inscription")
    public String afficherFormulaire() {
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscription(@RequestParam String username, @RequestParam String password, @RequestParam String email){
        User user=new User();
        user.setIdentifiant(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(RoleUser.UTILISATEUR);

        userService.ajouterUser(user);

        return "redirect:/login";
    }


}
