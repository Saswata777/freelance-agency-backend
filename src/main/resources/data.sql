-- ─────────────────────────────────────────────────────────────
-- PROJECTS SEED DATA
-- ─────────────────────────────────────────────────────────────

INSERT INTO projects (name, description, tags, demo_link, price_range, tech_stack, image_url, active)
VALUES
(
    'Customer Support Chatbot',
    'A rule-based chatbot for handling customer FAQs, support tickets, and auto-replies. Reduces support workload by 60%.',
    ARRAY['chatbot', 'bot', 'support', 'faq', 'chat', 'assistant', 'customer'],
    'https://demo.yourdomain.com/chatbot',
    '₹15,000 - ₹25,000',
    'React, Spring Boot, PostgreSQL',
    '/images/chatbot.png',
    true
),
(
    'E-Commerce Store',
    'Full-featured online store with product listings, cart, order management, and payment gateway integration.',
    ARRAY['ecommerce', 'shop', 'store', 'product', 'cart', 'order', 'buy', 'sell', 'payment'],
    'https://demo.yourdomain.com/ecommerce',
    '₹30,000 - ₹60,000',
    'React, Spring Boot, PostgreSQL, Razorpay',
    '/images/ecommerce.png',
    true
),
(
    'Analytics Dashboard',
    'Real-time business analytics dashboard with charts, reports, KPIs, and data export features.',
    ARRAY['dashboard', 'analytics', 'report', 'graph', 'chart', 'data', 'admin', 'kpi'],
    'https://demo.yourdomain.com/dashboard',
    '₹20,000 - ₹40,000',
    'React, Spring Boot, PostgreSQL, Chart.js',
    '/images/dashboard.png',
    true
),
(
    'Email Automation System',
    'Automated email campaign system with scheduling, templates, open tracking, and bulk sending.',
    ARRAY['email', 'mail', 'newsletter', 'campaign', 'smtp', 'notification', 'automation'],
    'https://demo.yourdomain.com/email',
    '₹12,000 - ₹20,000',
    'Spring Boot, JavaMail, PostgreSQL',
    '/images/email.png',
    true
),
(
    'Appointment Booking System',
    'Online booking platform with time slot management, calendar integration, reminders, and conflict prevention.',
    ARRAY['booking', 'appointment', 'schedule', 'slot', 'calendar', 'reservation'],
    'https://demo.yourdomain.com/booking',
    '₹18,000 - ₹30,000',
    'React, Spring Boot, PostgreSQL',
    '/images/booking.png',
    true
),
(
    'Business Landing Page',
    'High-converting landing page with modern design, contact form, testimonials, and SEO optimization.',
    ARRAY['website', 'landing', 'portfolio', 'blog', 'page', 'site', 'seo'],
    'https://demo.yourdomain.com/landing',
    '₹8,000 - ₹15,000',
    'React, Tailwind CSS',
    '/images/landing.png',
    true
),
(
    'Payment Gateway Integration',
    'Seamless Razorpay or Stripe integration with order management, webhook handling, and refund support.',
    ARRAY['payment', 'gateway', 'razorpay', 'stripe', 'invoice', 'billing', 'refund'],
    'https://demo.yourdomain.com/payment',
    '₹10,000 - ₹18,000',
    'Spring Boot, Razorpay, PostgreSQL',
    '/images/payment.png',
    true
),
(
    'REST API Backend',
    'Scalable REST API backend with authentication, role-based access, rate limiting, and full documentation.',
    ARRAY['api', 'rest', 'backend', 'server', 'integration', 'microservice', 'jwt'],
    'https://demo.yourdomain.com/api',
    '₹20,000 - ₹45,000',
    'Spring Boot, PostgreSQL, JWT',
    '/images/api.png',
    true
),
(
    'Workflow Automation Tool',
    'Custom automation scripts and workflows for repetitive business tasks, cron jobs, and data pipelines.',
    ARRAY['automation', 'automate', 'workflow', 'script', 'cron', 'scheduler', 'pipeline'],
    'https://demo.yourdomain.com/automation',
    '₹15,000 - ₹30,000',
    'Spring Boot, Python, PostgreSQL',
    '/images/automation.png',
    true
),
(
    'Mobile App (Flutter)',
    'Cross-platform mobile application for Android and iOS with clean UI, API integration, and push notifications.',
    ARRAY['mobile', 'app', 'android', 'ios', 'flutter', 'react native', 'cross platform'],
    'https://demo.yourdomain.com/mobile',
    '₹40,000 - ₹80,000',
    'Flutter, Dart, Spring Boot',
    '/images/mobile.png',
    true
);

-- ─────────────────────────────────────────────────────────────
-- PROJECT TAGS (separate table — mapped by JPA @ElementCollection)
-- ─────────────────────────────────────────────────────────────
-- Note: Tags are auto-inserted by Hibernate via @ElementCollection
-- The ARRAY[] values above are handled by JPA — no manual insert needed

-- ─────────────────────────────────────────────────────────────
-- SAMPLE LEAD (for testing)
-- ─────────────────────────────────────────────────────────────
INSERT INTO leads (name, email, requirement_text, detected_intent, status, created_at, updated_at)
VALUES (
    'Test User',
    'test@example.com',
    'I need a chatbot for my customer support team',
    'chatbot',
    'LEAD_CREATED',
    NOW(),
    NOW()
);