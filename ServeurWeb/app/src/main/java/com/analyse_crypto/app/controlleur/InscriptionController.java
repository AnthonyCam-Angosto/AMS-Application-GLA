package com.analyse_crypto.app.controlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.analyse_crypto.app.config.UserService;
import com.analyse_crypto.app.config.exception.EmailAlreadyUsedException;
import com.analyse_crypto.app.config.exception.IdentifiantAlreadyUsedException;
import com.analyse_crypto.app.tables.utilisateurs.User;

/**
 * Contrôleur gérant l'inscription des utilisateurs.
 *
 * Expose les endpoints d'affichage et de traitement du formulaire
 * d'inscription et délègue la logique métier à `UserService`.
 */
@Controller
public class InscriptionController {

    @Autowired
    UserService userService;

    /** Affiche le formulaire d'inscription. */
    @GetMapping("/inscription")
    public String afficherFormulaire(Model model) {
        model.addAttribute("user",new User());
        return "inscription";
    }

    /**
     * Traite l'envoi du formulaire d'inscription. Lance des exceptions
     * métier si l'email ou l'identifiant sont déjà utilisés.
     */
    @PostMapping("/inscription")
    public String inscription(@Validated @ModelAttribute("user") User user,BindingResult result){
        try {
            userService.verificationUser(user);
        } catch (EmailAlreadyUsedException e) {
            result.rejectValue("email", "error.user","Email déjà utilisé");
        }catch (IdentifiantAlreadyUsedException e) {
            result.rejectValue("identifiant", "error.user","identifiant déjà utilisé");
        }

        if(result.hasErrors()){
            return "inscription";
        }
        userService.ajouterUser(user);

        return "redirect:/login";
    }




}
