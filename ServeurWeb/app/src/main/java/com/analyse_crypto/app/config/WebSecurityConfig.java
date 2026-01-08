package com.analyse_crypto.app.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité Spring Security pour l'application.
 *
 * Définit les règles d'accès, la page de login et l'encodeur de mots de passe.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(requests ->{

        requests.requestMatchers("/admin/menu").hasRole("ADMIN");
        requests.requestMatchers("/styles/**","/inscription","/","/js/**").permitAll();
        requests.requestMatchers("/dashboard","/dashboard/**").permitAll();
        requests.anyRequest().authenticated();
        
        })
        .formLogin(form ->form.loginPage("/login").permitAll().defaultSuccessUrl("/"))
        .logout(logout->logout.permitAll().logoutSuccessUrl("/login?logout"))
        .headers(headers->headers
            .frameOptions(frameOptions->frameOptions.sameOrigin())
            .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                                        "script-src 'self' https://cdn.jsdelivr.net; " +
                                        "style-src 'self' https://fonts.googleapis.com; " +
                                        "font-src 'self' https://fonts.gstatic.com; " +
                                        "img-src 'self' data:; " +
                                        "object-src 'none'; " +
                                        "base-uri 'self'; " +
                                        "form-action 'self'; " +
                                        "frame-ancestors 'self'; " +
                                        "upgrade-insecure-requests")
                    )
                );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> servletContext.setSessionTrackingModes(
            java.util.Collections.singleton(jakarta.servlet.SessionTrackingMode.COOKIE)
    );
}

}
