package com.freelance.agency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF — we use stateless REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Route permissions
                .authorizeHttpRequests(auth -> auth

                        // ── Public endpoints ──────────────────────────────
                        .requestMatchers(
                                "/api/v1/chat/**",
                                "/api/v1/projects/**",
                                "/api/v1/testimonials",
                                "/api/v1/payments/webhook"
                        ).permitAll()

                        // ── Booking — public (users book calls) ──────────
                        .requestMatchers("/api/v1/bookings").permitAll()

                        // ── Testimonial submit — public ───────────────────
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/testimonials"
                        ).permitAll()

                        // ── Admin endpoints — protected ───────────────────
                        .requestMatchers(
                                "/api/v1/leads/**",
                                "/api/v1/bookings/**",
                                "/api/v1/payments/**",
                                "/api/v1/testimonials/pending",
                                "/api/v1/testimonials/*/approve"
                        ).authenticated()

                        // ── Everything else — authenticated ───────────────
                        .anyRequest().authenticated()
                )

                // Basic auth for now (replace with JWT later)
                .httpBasic(org.springframework.security.config.Customizer.withDefaults());

        return http.build();
    }
}