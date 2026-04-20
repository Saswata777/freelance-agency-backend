package com.freelance.agency.repository;

import com.freelance.agency.entity.Booking;
import com.freelance.agency.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByLeadId(Long leadId);

    // Check if slot is already booked
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.scheduledAt = :scheduledAt AND b.status != 'CANCELLED'")
    boolean isSlotTaken(@Param("scheduledAt") LocalDateTime scheduledAt);

    // Find bookings where reminder not yet sent and meeting is within 1 hour
    @Query("SELECT b FROM Booking b WHERE b.reminderSent = false AND b.scheduledAt BETWEEN :now AND :oneHourLater AND b.status = 'CONFIRMED'")
    List<Booking> findBookingsForReminder(
            @Param("now") LocalDateTime now,
            @Param("oneHourLater") LocalDateTime oneHourLater
    );
}