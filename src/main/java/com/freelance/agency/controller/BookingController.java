package com.freelance.agency.controller;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.BookingRequest;
import com.freelance.agency.dto.response.BookingResponse;
import com.freelance.agency.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_BOOKINGS)
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {

        log.info("Booking request received for lead: {}", request.getLeadId());
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByLead(
            @PathVariable Long leadId) {
        return ResponseEntity.ok(bookingService.getBookingsByLead(leadId));
    }
}