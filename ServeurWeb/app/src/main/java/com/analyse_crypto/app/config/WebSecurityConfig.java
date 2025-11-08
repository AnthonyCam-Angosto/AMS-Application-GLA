package com.analyse_crypto.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(requests ->{

        requests.requestMatchers("/admin/menu").hasRole("ADMIN");
        requests.requestMatchers("/styles/**","/inscription","/","/js/**").permitAll();
        requests.anyRequest().authenticated();
        
        })
        .formLogin(form ->form.loginPage("/login").permitAll().defaultSuccessUrl("/"))
        .logout(logout->logout.permitAll().logoutSuccessUrl("/login?logout"))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers->headers.frameOptions(frameOptions->frameOptions.sameOrigin()));
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return defaultsForSpringSecurity_v5_8();
    }
}
