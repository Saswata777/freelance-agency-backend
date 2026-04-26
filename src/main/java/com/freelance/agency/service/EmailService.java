package com.freelance.agency.service;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final DateTimeUtil dateTimeUtil;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.admin-email}")
    private String adminEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // ─────────────────────────────────────────────────────────────
    // 1. BOOKING CONFIRMATION → USER
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendBookingConfirmationToUser(String toEmail,
                                              String userName,
                                              LocalDateTime scheduledAt) {
        String subject = AppConstants.EMAIL_SUBJECT_BOOKING_CONFIRM;
        String formattedTime = dateTimeUtil.formatForDisplay(scheduledAt);

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            background:#0F172A;color:#F8FAFC;padding:32px;border-radius:12px;">
                    <h2 style="color:#6366F1;">Your Call is Confirmed ✅</h2>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>Your consultation call has been successfully booked.</p>
                    <div style="background:#1E293B;padding:16px;border-radius:8px;margin:20px 0;">
                        <p style="margin:0;font-size:16px;">
                            📅 <strong>Date & Time:</strong> %s
                        </p>
                    </div>
                    <p>We'll send you a reminder 1 hour before the call.</p>
                    <p>If you need to reschedule, please reply to this email.</p>
                    <br/>
                    <a href="%s"
                       style="background:#6366F1;color:#fff;padding:12px 24px;
                              border-radius:8px;text-decoration:none;font-weight:bold;">
                        Visit Our Website
                    </a>
                    <br/><br/>
                    <p style="color:#94A3B8;font-size:12px;">
                        You're receiving this because you booked a call with us.
                    </p>
                </div>
                """.formatted(userName, formattedTime, frontendUrl);

        sendEmail(toEmail, subject, html);
    }

    // ─────────────────────────────────────────────────────────────
    // 2. BOOKING NOTIFICATION → ADMIN
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendAdminBookingNotification(String userEmail,
                                             String userName,
                                             LocalDateTime scheduledAt) {
        String subject = AppConstants.EMAIL_SUBJECT_ADMIN_NEW_BOOKING;
        String formattedTime = dateTimeUtil.formatForDisplay(scheduledAt);

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            background:#0F172A;color:#F8FAFC;padding:32px;border-radius:12px;">
                    <h2 style="color:#6366F1;">New Call Booked 📅</h2>
                    <div style="background:#1E293B;padding:16px;border-radius:8px;margin:20px 0;">
                        <p>👤 <strong>Name:</strong> %s</p>
                        <p>📧 <strong>Email:</strong> %s</p>
                        <p>📅 <strong>Scheduled At:</strong> %s</p>
                    </div>
                    <p>Log in to your admin dashboard to manage this booking.</p>
                </div>
                """.formatted(userName, userEmail, formattedTime);

        sendEmail(adminEmail, subject, html);
    }

    // ─────────────────────────────────────────────────────────────
    // 3. BOOKING REMINDER → USER (1 hour before)
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendBookingReminder(String toEmail,
                                    String userName,
                                    LocalDateTime scheduledAt) {
        String subject = AppConstants.EMAIL_SUBJECT_BOOKING_REMINDER;
        String formattedTime = dateTimeUtil.formatForDisplay(scheduledAt);

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            background:#0F172A;color:#F8FAFC;padding:32px;border-radius:12px;">
                    <h2 style="color:#F59E0B;">Your Call is in 1 Hour ⏰</h2>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>Just a reminder that your consultation call is starting soon.</p>
                    <div style="background:#1E293B;padding:16px;border-radius:8px;margin:20px 0;">
                        <p style="margin:0;font-size:16px;">
                            📅 <strong>Scheduled At:</strong> %s
                        </p>
                    </div>
                    <p>Please be ready a few minutes before the call.</p>
                    <br/>
                    <p style="color:#94A3B8;font-size:12px;">
                        If you need to reschedule, please contact us immediately.
                    </p>
                </div>
                """.formatted(userName, formattedTime);

        sendEmail(toEmail, subject, html);
    }

    // ─────────────────────────────────────────────────────────────
    // 4. PAYMENT CONFIRMATION → USER
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendPaymentConfirmation(String toEmail,
                                        String userName,
                                        String amount) {
        String subject = AppConstants.EMAIL_SUBJECT_PAYMENT_CONFIRM;

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            background:#0F172A;color:#F8FAFC;padding:32px;border-radius:12px;">
                    <h2 style="color:#10B981;">Payment Received 🎉</h2>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>We have successfully received your payment.</p>
                    <div style="background:#1E293B;padding:16px;border-radius:8px;margin:20px 0;">
                        <p style="margin:0;font-size:18px;">
                            💰 <strong>Amount Paid:</strong> ₹%s
                        </p>
                    </div>
                    <p>Our team will start working on your project immediately.</p>
                    <p>We'll keep you updated on the progress.</p>
                    <br/>
                    <p style="color:#94A3B8;font-size:12px;">
                        Please keep this email as your payment receipt.
                    </p>
                </div>
                """.formatted(userName, amount);

        sendEmail(toEmail, subject, html);
    }

    // ─────────────────────────────────────────────────────────────
    // 5. TESTIMONIAL REQUEST → USER (after project delivery)
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendTestimonialRequest(String toEmail,
                                       String userName,
                                       Long leadId) {
        String subject = AppConstants.EMAIL_SUBJECT_TESTIMONIAL_REQ;
        String testimonialLink = frontendUrl + "/testimonial?leadId=" + leadId;

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            background:#0F172A;color:#F8FAFC;padding:32px;border-radius:12px;">
                    <h2 style="color:#6366F1;">How was your experience? 🌟</h2>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>We hope you're enjoying your new project!</p>
                    <p>We'd love to hear your feedback. It takes less than 2 minutes
                       and helps us serve future clients better.</p>
                    <br/>
                    <a href="%s"
                       style="background:#6366F1;color:#fff;padding:12px 24px;
                              border-radius:8px;text-decoration:none;font-weight:bold;">
                        Share Your Feedback ⭐
                    </a>
                    <br/><br/>
                    <p style="color:#94A3B8;font-size:12px;">
                        This is a one-time request. You can ignore it if you prefer.
                    </p>
                </div>
                """.formatted(userName, testimonialLink);

        sendEmail(toEmail, subject, html);
    }

    // ─────────────────────────────────────────────────────────────
    // 6. NEW LEAD NOTIFICATION → ADMIN
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendAdminNewLeadNotification(String userEmail, String requirement) {
        String subject = AppConstants.EMAIL_SUBJECT_ADMIN_NEW_LEAD;

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            background:#0F172A;color:#F8FAFC;padding:32px;border-radius:12px;">
                    <h2 style="color:#6366F1;">New Lead Received 🔔</h2>
                    <div style="background:#1E293B;padding:16px;border-radius:8px;margin:20px 0;">
                        <p>📧 <strong>Email:</strong> %s</p>
                        <p>💬 <strong>Requirement:</strong> %s</p>
                    </div>
                    <p>Log in to your admin dashboard to follow up.</p>
                </div>
                """.formatted(userEmail, requirement);

        sendEmail(adminEmail, subject, html);
    }

    // ─────────────────────────────────────────────────────────────
    // CORE SEND METHOD
    // ─────────────────────────────────────────────────────────────
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent to: {} | Subject: {}", to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {} | Subject: {} | Error: {}",
                    to, subject, e.getMessage());
            // Don't throw — email failure should not break main flow
        }
    }
}