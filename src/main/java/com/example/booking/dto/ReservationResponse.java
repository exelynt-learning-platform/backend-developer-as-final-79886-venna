package com.example.booking.dto;

import com.example.booking.entity.*;
import java.math.BigDecimal;
import java.time.*;

public record ReservationResponse(Long id, Long resourceId, String resourceName, BigDecimal price,
                                  String username, ReservationStatus status,
                                  LocalDate startDate, LocalDate endDate, Instant createdAt) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(r.getId(), r.getResource().getId(), r.getResource().getName(),
                r.getPrice(), r.getUser().getUsername(), r.getStatus(),
                r.getStartDate(), r.getEndDate(), r.getCreatedAt());
    }
}
