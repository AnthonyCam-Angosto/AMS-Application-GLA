package com.analyse_crypto.app.controlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Controller()
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String emailChangeForm="EmailChangeForm";
    private final String passwordChangeForm="PasswordChangeForm";
    private final String page="profile";

    public class EmailChangeForm {
        @NotBlank(message = "L’email est requis.")
        @Email(message = "Format d’email invalide.")
        private String newEmail;

        public String getNewEmail() { return newEmail; }
        public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
    }

    public class PasswordChangeForm {
        @NotBlank(message = "L’ancien mot de passe est requis.")
        private String oldPassword;

        @NotBlank(message = "Le nouveau mot de passe est requis.")
        //@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }
        public String getOldPassword() {
            return oldPassword;
        }
        public void setNewPassword(String newPassword) { 
            this.newPassword = newPassword; 
        }
        public void setOldPassword(String oldPassword) { 
            this.oldPassword = oldPassword; 
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        model.addAttribute(emailChangeForm, new EmailChangeForm());
        model.addAttribute(passwordChangeForm, new PasswordChangeForm());
        return page;
    }



    @PostMapping("/profile/change-email")
    public String changeEmail(@Valid @ModelAttribute("EmailChangeForm") EmailChangeForm form,BindingResult result,Authentication authentication,Model model){
        if (result.hasErrors()) {
            result.rejectValue("newEmail", "email.format", "erreur format");
            model.addAttribute(emailChangeForm, form);
            model.addAttribute(passwordChangeForm, new PasswordChangeForm());
            return page;
        }

        String username = authentication.getName();
        User user = userRepository.findByIdentifiant(username).get();

        if(userRepository.existsByEmail(form.getNewEmail())){
            result.rejectValue("newEmail", "email.exists", "Cet email est déjà utilisé.");
            model.addAttribute(emailChangeForm, form);
            model.addAttribute(passwordChangeForm, new PasswordChangeForm());
            return page;
        }

        user.setEmail(form.getNewEmail());
        userRepository.save(user);

        model.addAttribute("successMsg", "Email mis à jour avec succès.");
        model.addAttribute(emailChangeForm, new EmailChangeForm());
        model.addAttribute(passwordChangeForm, new PasswordChangeForm());
        return page;
    }

    @PostMapping("/profile/change-password")
    public String changePassword( @Valid @ModelAttribute("PasswordChangeForm") PasswordChangeForm form,BindingResult bindingResult,Authentication authentication,Model model){
        
        if (bindingResult.hasErrors()) {
            bindingResult.rejectValue("oldPassword", "format.oldPassword", "erreur format");
            model.addAttribute(passwordChangeForm, form);
            model.addAttribute(emailChangeForm, new EmailChangeForm());
            return page;
        }

        String username = authentication.getName();
        User user = userRepository.findByIdentifiant(username).get();

        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            bindingResult.rejectValue("oldPassword", "invalid.oldPassword", "Ancien mot de passe incorrect.");
            model.addAttribute(passwordChangeForm, form);
            model.addAttribute(emailChangeForm, new EmailChangeForm());
            return page;
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);

        model.addAttribute("successMsg", "Mot de passe mis à jour avec succès.");
        model.addAttribute(emailChangeForm, new EmailChangeForm());
        model.addAttribute(passwordChangeForm, new PasswordChangeForm());
        return page;
    }
    
}
