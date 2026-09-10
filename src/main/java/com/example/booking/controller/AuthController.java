package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.AppUser;
import com.example.booking.repository.AppUserRepository;
import com.example.booking.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    public AuthController(AppUserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = users.findByUsername(request.username())
                .filter(u -> encoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        return new LoginResponse(jwt.generate(user), "Bearer", jwt.expirationMs(), user.getUsername(), user.getRole().name());
    }
}
