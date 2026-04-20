package com.freelance.agency.dto.response;

import com.freelance.agency.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long leadId;
    private String leadName;
    private String leadEmail;
    private LocalDateTime scheduledAt;
    private LocalDateTime endTime;
    private String meetingLink;
    private BookingStatus status;
    private LocalDateTime createdAt;
}