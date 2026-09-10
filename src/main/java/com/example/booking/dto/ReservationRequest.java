package com.example.booking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ReservationRequest(@NotNull Long resourceId,
                                 @NotNull @FutureOrPresent LocalDate startDate,
                                 @NotNull @Future LocalDate endDate) {}
