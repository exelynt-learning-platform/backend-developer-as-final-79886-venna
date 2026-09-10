package com.example.booking.dto;

public record LoginResponse(String token, String tokenType, long expiresIn, String username, String role) {}
