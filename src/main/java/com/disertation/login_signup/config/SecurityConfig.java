package com.disertation.login_signup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
            .authorizeHttpRequests(auth -> auth
                // Updated line including /dashboard.html and /api/sensor/**
                .requestMatchers("/", "/index.html", "/dashboard.html", "/api/auth/**", "/api/sensor/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Required for H2 console frame

        return http.build();
    }
}