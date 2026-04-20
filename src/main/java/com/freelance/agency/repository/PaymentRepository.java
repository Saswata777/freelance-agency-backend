package com.freelance.agency.repository;

import com.freelance.agency.entity.Payment;
import com.freelance.agency.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByLeadId(Long leadId);

    List<Payment> findByStatus(PaymentStatus status);

    // Prevent duplicate webhook processing
    boolean existsByRazorpayPaymentId(String razorpayPaymentId);
}