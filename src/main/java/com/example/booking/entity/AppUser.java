package com.example.booking.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(name = "uk_user_username", columnNames = "username"))
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String username;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Role role;

    protected AppUser() {}
    public AppUser(String username, String password, Role role) {
        this.username = username; this.password = password; this.role = role;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
}
