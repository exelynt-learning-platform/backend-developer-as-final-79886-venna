package com.example.booking.repository;

import com.example.booking.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r join fetch r.resource join fetch r.user where r.id = :id")
    java.util.Optional<Reservation> findDetailedById(@Param("id") Long id);

    @Query("select r from Reservation r join fetch r.resource join fetch r.user " +
           "where (:status is null or r.status = :status) " +
           "and (:minPrice is null or r.price >= :minPrice) " +
           "and (:maxPrice is null or r.price <= :maxPrice) " +
           "and (:username is null or r.user.username = :username)")
    Page<Reservation> search(@Param("status") ReservationStatus status,
                             @Param("minPrice") java.math.BigDecimal minPrice,
                             @Param("maxPrice") java.math.BigDecimal maxPrice,
                             @Param("username") String username, Pageable pageable);
}
