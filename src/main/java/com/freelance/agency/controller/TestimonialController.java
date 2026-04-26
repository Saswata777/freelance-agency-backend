package com.freelance.agency.controller;

import com.freelance.agency.constants.AppConstants;
import com.freelance.agency.dto.request.TestimonialRequest;
import com.freelance.agency.entity.Testimonial;
import com.freelance.agency.service.TestimonialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_TESTIMONIALS)
@RequiredArgsConstructor
@Slf4j
public class TestimonialController {

    private final TestimonialService testimonialService;

    // Public — submit testimonial
    @PostMapping
    public ResponseEntity<Testimonial> submitTestimonial(
            @Valid @RequestBody TestimonialRequest request) {

        log.info("Testimonial submitted by: {}", request.getName());
        Testimonial saved = testimonialService.submitTestimonial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Public — get approved testimonials (for frontend display)
    @GetMapping
    public ResponseEntity<List<Testimonial>> getApprovedTestimonials() {
        return ResponseEntity.ok(testimonialService.getApprovedTestimonials());
    }

    // Admin — get pending testimonials
    @GetMapping("/pending")
    public ResponseEntity<List<Testimonial>> getPendingTestimonials() {
        return ResponseEntity.ok(testimonialService.getPendingTestimonials());
    }

    // Admin — approve testimonial
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Testimonial> approveTestimonial(@PathVariable Long id) {
        return ResponseEntity.ok(testimonialService.approveTestimonial(id));
    }

    // Admin — delete testimonial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestimonial(@PathVariable Long id) {
        testimonialService.deleteTestimonial(id);
        return ResponseEntity.noContent().build();
    }
}