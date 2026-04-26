package com.freelance.agency.service;

import com.freelance.agency.dto.request.TestimonialRequest;
import com.freelance.agency.entity.Lead;
import com.freelance.agency.entity.Testimonial;
import com.freelance.agency.exception.ResourceNotFoundException;
import com.freelance.agency.repository.LeadRepository;
import com.freelance.agency.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final LeadRepository leadRepository;

    public Testimonial submitTestimonial(TestimonialRequest request) {

        Lead lead = null;
        if (request.getLeadId() != null) {
            lead = leadRepository.findById(request.getLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Lead not found with id: " + request.getLeadId()));
        }

        Testimonial testimonial = Testimonial.builder()
                .lead(lead)
                .name(request.getName())
                .company(request.getCompany())
                .review(request.getReview())
                .rating(request.getRating())
                .approved(false)
                .build();

        Testimonial saved = testimonialRepository.save(testimonial);
        log.info("Testimonial submitted by: {}", request.getName());
        return saved;
    }

    public List<Testimonial> getApprovedTestimonials() {
        return testimonialRepository.findByApprovedTrue();
    }

    public List<Testimonial> getPendingTestimonials() {
        return testimonialRepository.findByApprovedFalse();
    }

    public Testimonial approveTestimonial(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Testimonial not found with id: " + id));
        testimonial.setApproved(true);
        log.info("Testimonial approved: {}", id);
        return testimonialRepository.save(testimonial);
    }

    public void deleteTestimonial(Long id) {
        if (!testimonialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Testimonial not found with id: " + id);
        }
        testimonialRepository.deleteById(id);
        log.info("Testimonial deleted: {}", id);
    }
}