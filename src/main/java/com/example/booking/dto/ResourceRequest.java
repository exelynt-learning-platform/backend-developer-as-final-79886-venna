package com.example.booking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ResourceRequest(@NotBlank @Size(max = 150) String name,
                              @Size(max = 2000) String description,
                              @NotNull @DecimalMin(value = "0.00") @Digits(integer = 10, fraction = 2) BigDecimal price,
                              Boolean available) {}
