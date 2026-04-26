package com.freelance.agency.service;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.BookingRequest;
import com.freelance.agency.dto.response.BookingResponse;
import com.freelance.agency.entity.Booking;
import com.freelance.agency.entity.Lead;
import com.freelance.agency.enums.BookingStatus;
import com.freelance.agency.enums.LeadStatus;
import com.freelance.agency.exception.BadRequestException;
import com.freelance.agency.exception.ResourceNotFoundException;
import com.freelance.agency.mapper.BookingMapper;
import com.freelance.agency.repository.BookingRepository;
import com.freelance.agency.repository.LeadRepository;
import com.freelance.agency.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LeadRepository leadRepository;
    private final BookingMapper bookingMapper;
    private final EmailService emailService;
    private final DateTimeUtil dateTimeUtil;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        // Check if slot is already taken
        if (bookingRepository.isSlotTaken(request.getScheduledAt())) {
            throw new BadRequestException(
                    "This time slot is already booked. Please choose another time.");
        }

        // Find or create lead
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead not found with id: " + request.getLeadId()));

        // Update lead name/email if not set
        if (lead.getName() == null) lead.setName(request.getName());

        // Calculate end time
        LocalDateTime endTime = dateTimeUtil.getEndTime(
                request.getScheduledAt(),
                AppConstants.SLOT_DURATION_MINUTES);

        // Create booking
        Booking booking = Booking.builder()
                .lead(lead)
                .scheduledAt(request.getScheduledAt())
                .endTime(endTime)
                .notes(request.getNotes())
                .status(BookingStatus.CONFIRMED)
                .reminderSent(false)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Update lead status
        lead.setStatus(LeadStatus.CALL_BOOKED);
        leadRepository.save(lead);

        // Send confirmation emails
        emailService.sendBookingConfirmationToUser(
                request.getEmail(),
                request.getName(),
                request.getScheduledAt());

        emailService.sendAdminBookingNotification(
                request.getEmail(),
                request.getName(),
                request.getScheduledAt());

        log.info("Booking created for lead: {} at: {}", lead.getId(), request.getScheduledAt());

        return bookingMapper.toResponse(saved);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingMapper.toResponseList(bookingRepository.findAll());
    }

    public List<BookingResponse> getBookingsByLead(Long leadId) {
        return bookingMapper.toResponseList(bookingRepository.findByLeadId(leadId));
    }

    /**
     * Called by scheduler — send reminders for upcoming bookings
     */
    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusMinutes(AppConstants.REMINDER_MINUTES_BEFORE);

        List<Booking> bookings = bookingRepository
                .findBookingsForReminder(now, oneHourLater);

        for (Booking booking : bookings) {
            try {
                emailService.sendBookingReminder(
                        booking.getLead().getEmail(),
                        booking.getLead().getName(),
                        booking.getScheduledAt());

                booking.setReminderSent(true);
                bookingRepository.save(booking);

                log.info("Reminder sent for booking: {}", booking.getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for booking: {}", booking.getId(), e);
            }
        }
    }
}