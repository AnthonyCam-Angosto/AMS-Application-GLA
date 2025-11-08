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
        model.addAttribute("EmailChangeForm", new EmailChangeForm());
        model.addAttribute("PasswordChangeForm", new PasswordChangeForm());
        return "profile";
    }



    @PostMapping("/profile/change-email")
    public String change_email(@Valid @ModelAttribute("EmailChangeForm") EmailChangeForm form,BindingResult result,Authentication authentication,Model model){
        if (result.hasErrors()) {
            result.rejectValue("newEmail", "email.format", "erreur format");
            model.addAttribute("EmailChangeForm", form);
            model.addAttribute("PasswordChangeForm", new PasswordChangeForm());
            return "profile";
        }

        String username = authentication.getName();
        User user = userRepository.findByIdentifiant(username).get();

        if(userRepository.existsByEmail(form.getNewEmail())){
            result.rejectValue("newEmail", "email.exists", "Cet email est déjà utilisé.");
            model.addAttribute("EmailChangeForm", form);
            model.addAttribute("PasswordChangeForm", new PasswordChangeForm());
            return "profile";
        }

        user.setEmail(form.getNewEmail());
        userRepository.save(user);

        model.addAttribute("successMsg", "Email mis à jour avec succès.");
        model.addAttribute("EmailChangeForm", new EmailChangeForm());
        model.addAttribute("PasswordChangeForm", new PasswordChangeForm());
        return "profile";
    }

    @PostMapping("/profile/change-password")
    public String change_password( @Valid @ModelAttribute("PasswordChangeForm") PasswordChangeForm form,BindingResult bindingResult,Authentication authentication,Model model){
        
        if (bindingResult.hasErrors()) {
            bindingResult.rejectValue("oldPassword", "format.oldPassword", "erreur format");
            model.addAttribute("PasswordChangeForm", form);
            model.addAttribute("EmailChangeForm", new EmailChangeForm());
            return "profile";
        }

        String username = authentication.getName();
        User user = userRepository.findByIdentifiant(username).get();

        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            bindingResult.rejectValue("oldPassword", "invalid.oldPassword", "Ancien mot de passe incorrect.");
            model.addAttribute("passwordChangeForm", form);
            model.addAttribute("EmailChangeForm", new EmailChangeForm());
            return "profile";
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);

        model.addAttribute("successMsg", "Mot de passe mis à jour avec succès.");
        model.addAttribute("EmailChangeForm", new EmailChangeForm());
        model.addAttribute("PasswordChangeForm", new PasswordChangeForm());
        return "profile";
    }
    
}
