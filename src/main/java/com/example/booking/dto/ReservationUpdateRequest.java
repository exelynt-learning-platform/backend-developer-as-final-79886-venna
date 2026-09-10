package com.example.booking.dto;

import com.example.booking.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record ReservationUpdateRequest(@NotNull ReservationStatus status) {}
