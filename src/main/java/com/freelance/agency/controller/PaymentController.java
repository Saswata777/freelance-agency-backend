package com.freelance.agency.controller;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.PaymentRequest;
import com.freelance.agency.dto.response.PaymentResponse;
import com.freelance.agency.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(AppConstants.API_PAYMENTS)
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // Create Razorpay order
    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponse> createOrder(
            @Valid @RequestBody PaymentRequest request) {

        log.info("Payment order request for lead: {}", request.getLeadId());
        PaymentResponse response = paymentService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Razorpay webhook
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Razorpay-Signature",
                    required = false) String signature) {

        log.info("Webhook received from Razorpay");
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed");
    }

    // Get payments by lead
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLead(
            @PathVariable Long leadId) {
        return ResponseEntity.ok(paymentService.getPaymentsByLead(leadId));
    }

    // Get all payments (admin)
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}