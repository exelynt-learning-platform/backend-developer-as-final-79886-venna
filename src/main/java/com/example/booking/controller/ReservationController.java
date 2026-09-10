package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.*;
import com.example.booking.exception.ForbiddenException;
import com.example.booking.service.ReservationService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Set;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService service;
    public ReservationController(ReservationService service) { this.service = service; }

    @GetMapping
    public Page<ReservationResponse> list(@RequestParam(required = false) ReservationStatus status,
                                          @RequestParam(required = false) BigDecimal minPrice,
                                          @RequestParam(required = false) BigDecimal maxPrice,
                                          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                          Authentication authentication) {
        String username = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? null : authentication.getName();
        return service.search(status, minPrice, maxPrice, username, normalized(pageable));
    }

    @GetMapping("/{id}")
    public ReservationResponse get(@PathVariable Long id, Authentication authentication) {
        Reservation reservation = service.get(id);
        verifyOwnerOrAdmin(reservation, authentication);
        return ReservationResponse.from(reservation);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationRequest request, Authentication authentication) {
        return service.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationResponse update(@PathVariable Long id, @Valid @RequestBody ReservationUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    private void verifyOwnerOrAdmin(Reservation reservation, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !reservation.getUser().getUsername().equals(authentication.getName()))
            throw new ForbiddenException("You may only access your own reservations");
    }

    private Pageable normalized(Pageable pageable) {
        Set<String> allowed = Set.of("createdAt", "status", "startDate", "endDate", "price");
        List<Sort.Order> orders = pageable.getSort().stream()
                .filter(order -> allowed.contains(order.getProperty()))
                .toList();
        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort.isSorted() ? sort : Sort.by(Sort.Order.desc("createdAt")));
    }
}
