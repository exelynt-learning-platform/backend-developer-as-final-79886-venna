package com.example.booking.service;

import com.example.booking.entity.AppUser;
import com.example.booking.exception.NotFoundException;
import com.example.booking.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AppUserRepository users;
    public UserService(AppUserRepository users) { this.users = users; }
    public AppUser byUsername(String username) {
        return users.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
