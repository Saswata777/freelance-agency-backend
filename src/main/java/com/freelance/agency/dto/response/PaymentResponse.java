package com.freelance.agency.dto.response;

import com.freelance.agency.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long leadId;
    private BigDecimal amount;
    private String currency;
    private String razorpayOrderId;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}