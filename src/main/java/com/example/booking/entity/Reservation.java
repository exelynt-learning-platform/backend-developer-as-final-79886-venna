package com.example.booking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Reservation() {}
    public Reservation(Resource resource, AppUser user, LocalDate startDate, LocalDate endDate) {
        this.resource = resource; this.user = user; this.price = resource.getPrice();
        this.startDate = startDate; this.endDate = endDate;
    }
    public Long getId() { return id; }
    public Resource getResource() { return resource; }
    public AppUser getUser() { return user; }
    public BigDecimal getPrice() { return price; }
    public ReservationStatus getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
