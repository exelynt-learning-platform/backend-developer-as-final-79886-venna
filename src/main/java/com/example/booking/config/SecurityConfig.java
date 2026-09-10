package com.example.booking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.booking.security.JwtAuthenticationFilter;
import com.example.booking.repository.AppUserRepository;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService userDetailsService(AppUserRepository users) {
        return username -> users.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername()).password(user.getPassword())
                        .roles(user.getRole().name()).build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter, ObjectMapper mapper) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/resources/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                        res.setStatus(401); res.setContentType("application/json");
                        mapper.writeValue(res.getOutputStream(), Map.of("timestamp", Instant.now(), "status", 401,
                                "error", "Unauthorized", "message", "Authentication required", "path", req.getRequestURI()));
                    })
                    .accessDeniedHandler((req, res, ex) -> {
                        res.setStatus(403); res.setContentType("application/json");
                        mapper.writeValue(res.getOutputStream(), Map.of("timestamp", Instant.now(), "status", 403,
                                "error", "Forbidden", "message", "Access denied", "path", req.getRequestURI()));
                    }));
        return http.build();
    }
}
