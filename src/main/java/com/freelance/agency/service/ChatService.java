package com.freelance.agency.service;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.RequirementRequest;
import com.freelance.agency.dto.response.AnalysisResponse;
import com.freelance.agency.entity.Lead;
import com.freelance.agency.enums.LeadStatus;
import com.freelance.agency.repository.LeadRepository;
import com.freelance.agency.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final AnalysisService analysisService;
    private final LeadRepository leadRepository;
    private final EmailService emailService;
    private final ValidationUtil validationUtil;

    /**
     * Process user message:
     * 1. Validate input
     * 2. Analyze requirement
     * 3. Save lead if email provided
     * 4. Notify admin
     * 5. Return analysis
     */
    public AnalysisResponse processMessage(RequirementRequest request) {

        String message = validationUtil.sanitize(request.getMessage());
        log.info("Processing message: {}", message);

        // Analyze the requirement
        AnalysisResponse response = analysisService.analyze(message);

        // Save lead if email is provided
        if (!validationUtil.isBlank(request.getEmail())) {
            saveLead(request, response.getIntent());
            notifyAdmin(request.getEmail(), message);
        }

        return response;
    }

    /**
     * Save or update lead in DB
     */
    private void saveLead(RequirementRequest request, String intent) {
        try {
            // If lead already exists with same email, update requirement
            Lead lead = leadRepository.findByEmail(request.getEmail())
                    .orElse(Lead.builder()
                            .email(request.getEmail())
                            .status(LeadStatus.LEAD_CREATED)
                            .build());

            lead.setName(request.getName());
            lead.setRequirementText(request.getMessage());
            lead.setDetectedIntent(intent);

            leadRepository.save(lead);
            log.info("Lead saved for email: {}", request.getEmail());

        } catch (Exception e) {
            log.error("Failed to save lead for email: {}", request.getEmail(), e);
        }
    }

    /**
     * Notify admin about new lead
     */
    private void notifyAdmin(String userEmail, String requirement) {
        try {
            emailService.sendAdminNewLeadNotification(userEmail, requirement);
        } catch (Exception e) {
            log.error("Failed to send admin notification for lead: {}", userEmail, e);
        }
    }
}