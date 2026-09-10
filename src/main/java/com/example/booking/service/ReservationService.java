package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.entity.*;
import com.example.booking.exception.*;
import com.example.booking.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class ReservationService {
    private final ReservationRepository reservations;
    private final ResourceService resourceService;
    private final UserService userService;

    public ReservationService(ReservationRepository reservations, ResourceService resourceService, UserService userService) {
        this.reservations = reservations; this.resourceService = resourceService; this.userService = userService;
    }
    @Transactional(readOnly = true)
    public Page<ReservationResponse> search(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice,
                                            String username, Pageable pageable) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0)
            throw new BadRequestException("minPrice must not exceed maxPrice");
        return reservations.search(status, minPrice, maxPrice, username, pageable).map(ReservationResponse::from);
    }
    @Transactional
    public ReservationResponse create(ReservationRequest request, String username) {
        Resource resource = resourceService.get(request.resourceId());
        if (!resource.isAvailable()) throw new BadRequestException("Resource is not available");
        if (!request.endDate().isAfter(request.startDate())) throw new BadRequestException("endDate must be after startDate");
        AppUser user = userService.byUsername(username);
        return ReservationResponse.from(reservations.save(new Reservation(resource, user, request.startDate(), request.endDate())));
    }
    @Transactional(readOnly = true)
    public Reservation get(Long id) { return reservations.findDetailedById(id).orElseThrow(() -> new NotFoundException("Reservation " + id + " not found")); }
    @Transactional
    public ReservationResponse update(Long id, ReservationUpdateRequest request) {
        Reservation reservation = get(id);
        reservation.setStatus(request.status());
        return ReservationResponse.from(reservations.save(reservation));
    }
    @Transactional
    public void delete(Long id) { reservations.delete(get(id)); }
}
