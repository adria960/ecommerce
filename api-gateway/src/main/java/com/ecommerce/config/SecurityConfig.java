package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults()))

                .build();
    }
}

/*
    Configuration explained
        Configuration	        Purpose
        ------------------------------------------------------------------------------------
        csrf().disable()	    Disables CSRF protection for our bearer-token REST API
        STATELESS	            Does not use HTTP sessions for authentication
        permitAll()	            Allows unauthenticated access
        authenticated()	        Requires valid authentication
        oauth2ResourceServer()	Configures OAuth2 access token validation
        jwt()	                Enables JWT-based authentication

        We disable CSRF because our current API uses bearer tokens in the Authorization header,
        not cookie-based authentication.

        At this stage, every authenticated user can access every API.
        We will introduce role-based authorization below.
         */