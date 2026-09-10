package com.example.booking.config;

import com.example.booking.entity.AppUser;
import com.example.booking.entity.Resource;
import com.example.booking.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(AppUserRepository users, ResourceRepository resources, PasswordEncoder encoder,
                           @Value("${app.seed.enabled:true}") boolean enabled,
                           @Value("${app.seed.admin-username:admin}") String adminName,
                           @Value("${app.seed.admin-password:Admin123!}") String adminPassword,
                           @Value("${app.seed.user-username:user}") String userName,
                           @Value("${app.seed.user-password:User123!}") String userPassword) {
        return args -> {
            if (!enabled) return;
            if (!users.existsByUsername(adminName)) users.save(new AppUser(adminName, encoder.encode(adminPassword), com.example.booking.entity.Role.ADMIN));
            if (!users.existsByUsername(userName)) users.save(new AppUser(userName, encoder.encode(userPassword), com.example.booking.entity.Role.USER));
            if (resources.count() == 0) resources.save(new Resource("Conference Room", "Seed resource", new BigDecimal("50.00"), true));
        };
    }
}
