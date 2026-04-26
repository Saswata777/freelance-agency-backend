package com.freelance.agency.service;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.PaymentRequest;
import com.freelance.agency.dto.response.PaymentResponse;
import com.freelance.agency.entity.Lead;
import com.freelance.agency.entity.Payment;
import com.freelance.agency.enums.LeadStatus;
import com.freelance.agency.enums.PaymentStatus;
import com.freelance.agency.exception.PaymentException;
import com.freelance.agency.exception.ResourceNotFoundException;
import com.freelance.agency.mapper.PaymentMapper;
import com.freelance.agency.repository.LeadRepository;
import com.freelance.agency.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LeadRepository leadRepository;
    private final PaymentMapper paymentMapper;
    private final EmailService emailService;

    @Value("${app.payment.razorpay-key-id}")
    private String razorpayKeyId;

    @Value("${app.payment.razorpay-key-secret}")
    private String razorpayKeySecret;

    // ─────────────────────────────────────────────────────────────
    // 1. CREATE RAZORPAY ORDER
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public PaymentResponse createOrder(PaymentRequest request) {

        // Find lead
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead not found with id: " + request.getLeadId()));

        try {
            // Init Razorpay client
            RazorpayClient razorpayClient = new RazorpayClient(
                    razorpayKeyId, razorpayKeySecret);

            // Amount in paise (INR * 100)
            int amountInPaise = request.getAmount()
                    .multiply(BigDecimal.valueOf(AppConstants.RAZORPAY_FACTOR))
                    .intValue();

            // Build Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", "receipt_lead_" + request.getLeadId());
            orderRequest.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            log.info("Razorpay order created: {} for lead: {}",
                    razorpayOrderId, request.getLeadId());

            // Save payment record in DB
            Payment payment = Payment.builder()
                    .lead(lead)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .razorpayOrderId(razorpayOrderId)
                    .status(PaymentStatus.PENDING)
                    .build();

            Payment saved = paymentRepository.save(payment);

            // Update lead status
            lead.setStatus(LeadStatus.PAYMENT_PENDING);
            leadRepository.save(lead);

            return paymentMapper.toResponse(saved);

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed for lead: {} | Error: {}",
                    request.getLeadId(), e.getMessage());
            throw new PaymentException("Failed to create payment order. Please try again.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. HANDLE PAYMENT WEBHOOK (success / failure)
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public void handleWebhook(Map<String, Object> payload, String razorpaySignature) {

        try {
            // Extract event type
            String event = (String) payload.get("event");
            log.info("Razorpay webhook received: {}", event);

            if ("payment.captured".equals(event)) {
                handlePaymentSuccess(payload, razorpaySignature);
            } else if ("payment.failed".equals(event)) {
                handlePaymentFailure(payload);
            } else {
                log.info("Unhandled webhook event: {}", event);
            }

        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage());
            throw new PaymentException("Webhook processing failed.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 3. PAYMENT SUCCESS
    // ─────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void handlePaymentSuccess(Map<String, Object> payload,
                                      String razorpaySignature) {

        Map<String, Object> paymentEntity = extractPaymentEntity(payload);

        String razorpayPaymentId = (String) paymentEntity.get("id");
        String razorpayOrderId   = (String) paymentEntity.get("order_id");

        // ── Duplicate webhook guard ──────────────────────────────
        if (paymentRepository.existsByRazorpayPaymentId(razorpayPaymentId)) {
            log.warn("Duplicate webhook ignored for paymentId: {}", razorpayPaymentId);
            return;
        }

        // ── Verify signature ─────────────────────────────────────
        boolean isValid = verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        if (!isValid) {
            log.error("Invalid Razorpay signature for paymentId: {}", razorpayPaymentId);
            throw new PaymentException("Invalid payment signature.");
        }

        // ── Find payment record ──────────────────────────────────
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for orderId: " + razorpayOrderId));

        // ── Update payment ───────────────────────────────────────
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        // ── Update lead status ───────────────────────────────────
        Lead lead = payment.getLead();
        lead.setStatus(LeadStatus.PAYMENT_COMPLETED);
        leadRepository.save(lead);

        // ── Send payment confirmation email ──────────────────────
        emailService.sendPaymentConfirmation(
                lead.getEmail(),
                lead.getName() != null ? lead.getName() : "Valued Client",
                payment.getAmount().toString());

        log.info("Payment success processed for paymentId: {}", razorpayPaymentId);
    }

    // ─────────────────────────────────────────────────────────────
    // 4. PAYMENT FAILURE
    // ─────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void handlePaymentFailure(Map<String, Object> payload) {

        Map<String, Object> paymentEntity = extractPaymentEntity(payload);

        String razorpayOrderId = (String) paymentEntity.get("order_id");
        String errorDesc       = (String) paymentEntity.getOrDefault(
                "error_description", "Payment failed");

        paymentRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(errorDesc);
            paymentRepository.save(payment);

            // Update lead status back
            Lead lead = payment.getLead();
            lead.setStatus(LeadStatus.PAYMENT_PENDING);
            leadRepository.save(lead);

            log.warn("Payment failed for orderId: {} | Reason: {}",
                    razorpayOrderId, errorDesc);
        });
    }

    // ─────────────────────────────────────────────────────────────
    // 5. VERIFY RAZORPAY SIGNATURE
    // ─────────────────────────────────────────────────────────────
    private boolean verifySignature(String orderId,
                                    String paymentId,
                                    String signature) {
        try {
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(hash);

            return generatedSignature.equals(signature);

        } catch (Exception e) {
            log.error("Signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 6. HELPER — Extract payment entity from webhook payload
    // ─────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractPaymentEntity(Map<String, Object> payload) {
        Map<String, Object> data = (Map<String, Object>) payload.get("payload");
        Map<String, Object> paymentWrapper = (Map<String, Object>) data.get("payment");
        return (Map<String, Object>) paymentWrapper.get("entity");
    }

    // ─────────────────────────────────────────────────────────────
    // 7. GET PAYMENTS
    // ─────────────────────────────────────────────────────────────
    public List<PaymentResponse> getPaymentsByLead(Long leadId) {
        return paymentMapper.toResponseList(
                paymentRepository.findByLeadId(leadId));
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentMapper.toResponseList(paymentRepository.findAll());
    }
}