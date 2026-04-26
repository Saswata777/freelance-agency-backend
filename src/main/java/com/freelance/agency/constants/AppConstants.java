package com.freelance.agency.constants;

public final class AppConstants {

    // Prevent instantiation
    private AppConstants() {}

    // ─── API PATHS ───────────────────────────────────────────
    public static final String API_BASE          = "/api/v1";
    public static final String API_CHAT          = API_BASE + "/chat";
    public static final String API_PROJECTS      = API_BASE + "/projects";
    public static final String API_BOOKINGS      = API_BASE + "/bookings";
    public static final String API_PAYMENTS      = API_BASE + "/payments";
    public static final String API_TESTIMONIALS  = API_BASE + "/testimonials";
    public static final String API_LEADS         = API_BASE + "/leads";

    // ─── BOOKING ─────────────────────────────────────────────
    public static final int SLOT_DURATION_MINUTES   = 60;
    public static final int MAX_BOOKING_DAYS_AHEAD  = 14;
    public static final int REMINDER_MINUTES_BEFORE = 60;

    // ─── ANALYSIS ENGINE ─────────────────────────────────────
    public static final int MIN_MATCH_SCORE         = 1;
    public static final int MAX_RECOMMENDATIONS     = 2;

    // ─── LEAD STATUS MESSAGES ────────────────────────────────
    public static final String MSG_NO_MATCH =
            "We'll need a custom solution for your requirement. Book a call so we can understand your needs better.";

    public static final String MSG_MATCH_FOUND =
            "Great! We found some solutions that match your requirement.";

    public static final String MSG_EMPTY_INPUT =
            "Please describe your requirement so we can help you better.";

    public static final String MSG_SPAM_INPUT =
            "Please enter a valid description of what you need.";

    // ─── EMAIL SUBJECTS ──────────────────────────────────────
    public static final String EMAIL_SUBJECT_BOOKING_CONFIRM  = "Your Call is Booked ✅";
    public static final String EMAIL_SUBJECT_BOOKING_REMINDER = "Reminder: Your Call is in 1 Hour ⏰";
    public static final String EMAIL_SUBJECT_PAYMENT_CONFIRM  = "Payment Received 🎉";
    public static final String EMAIL_SUBJECT_TESTIMONIAL_REQ  = "How was your experience? 🌟";
    public static final String EMAIL_SUBJECT_ADMIN_NEW_LEAD   = "New Lead Received 🔔";
    public static final String EMAIL_SUBJECT_ADMIN_NEW_BOOKING= "New Call Booked 📅";

    // ─── PAYMENT ─────────────────────────────────────────────
    public static final String CURRENCY_INR    = "INR";
    public static final int    RAZORPAY_FACTOR = 100; // paise conversion

    // ─── PAGINATION ──────────────────────────────────────────
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE     = 50;

    // ─── MISC ─────────────────────────────────────────────────
    public static final String DATE_FORMAT_DISPLAY = "dd MMM yyyy, hh:mm a";
    public static final String DATE_FORMAT_LOG     = "yyyy-MM-dd HH:mm:ss";
}