package com.freelance.agency.controller;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.entity.Lead;
import com.freelance.agency.enums.LeadStatus;
import com.freelance.agency.exception.ResourceNotFoundException;
import com.freelance.agency.repository.LeadRepository;
import com.freelance.agency.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_LEADS)
@RequiredArgsConstructor
@Slf4j
public class LeadController {

    private final LeadRepository leadRepository;
    private final EmailService emailService;

    // Admin — get all leads
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        return ResponseEntity.ok(leadRepository.findAll());
    }

    // Admin — get lead by id
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead not found with id: " + id));
        return ResponseEntity.ok(lead);
    }

    // Admin — update lead status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Lead> updateLeadStatus(
            @PathVariable Long id,
            @RequestParam LeadStatus status) {

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead not found with id: " + id));

        lead.setStatus(status);
        Lead updated = leadRepository.save(lead);
        log.info("Lead {} status updated to: {}", id, status);
        return ResponseEntity.ok(updated);
    }

    // Admin — request testimonial manually
    @PostMapping("/{id}/request-testimonial")
    public ResponseEntity<String> requestTestimonial(@PathVariable Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead not found with id: " + id));

        emailService.sendTestimonialRequest(
                lead.getEmail(),
                lead.getName() != null ? lead.getName() : "Valued Client",
                lead.getId());

        lead.setStatus(LeadStatus.TESTIMONIAL_REQUESTED);
        leadRepository.save(lead);

        log.info("Testimonial request sent for lead: {}", id);
        return ResponseEntity.ok("Testimonial request sent successfully.");
    }
}