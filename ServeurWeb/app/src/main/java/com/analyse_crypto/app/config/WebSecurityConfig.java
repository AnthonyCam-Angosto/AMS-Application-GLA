package com.analyse_crypto.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(requests ->{

        requests.requestMatchers("/admin/menu").hasRole("ADMIN");
        requests.requestMatchers("/styles/**","/inscription","/").permitAll();
        requests.anyRequest().authenticated();
        
        })
        .formLogin((form) ->form.loginPage("/login").permitAll().defaultSuccessUrl("/"))
        .logout(logout->logout.permitAll().logoutSuccessUrl("/login?logout"))
        .csrf(AbstractHttpConfigurer::disable)
        .headers((headers)->headers.frameOptions((frameOptions)->frameOptions.sameOrigin()));
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
